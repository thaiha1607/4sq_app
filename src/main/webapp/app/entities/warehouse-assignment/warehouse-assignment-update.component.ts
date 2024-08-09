import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import WarehouseAssignmentService from './warehouse-assignment.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import WorkingUnitService from '@/entities/working-unit/working-unit.service';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import { type IWarehouseAssignment, WarehouseAssignment } from '@/shared/model/warehouse-assignment.model';
import { AssignmentStatus } from '@/shared/model/enumerations/assignment-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'WarehouseAssignmentUpdate',
  setup() {
    const warehouseAssignmentService = inject('warehouseAssignmentService', () => new WarehouseAssignmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const warehouseAssignment: Ref<IWarehouseAssignment> = ref(new WarehouseAssignment());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());

    const workingUnits: Ref<IWorkingUnit[]> = ref([]);

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);
    const assignmentStatusValues: Ref<string[]> = ref(Object.keys(AssignmentStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveWarehouseAssignment = async warehouseAssignmentId => {
      try {
        const res = await warehouseAssignmentService().find(warehouseAssignmentId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        warehouseAssignment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.warehouseAssignmentId) {
      retrieveWarehouseAssignment(route.params.warehouseAssignmentId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      workingUnitService()
        .retrieve()
        .then(res => {
          workingUnits.value = res.data;
        });
      orderService()
        .retrieve()
        .then(res => {
          orders.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      status: {
        required: validations.required('This field is required.'),
      },
      note: {},
      otherInfo: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {},
      sourceWorkingUnit: {
        required: validations.required('This field is required.'),
      },
      targetWorkingUnit: {},
      order: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, warehouseAssignment as any);
    v$.value.$validate();

    return {
      warehouseAssignmentService,
      alertService,
      warehouseAssignment,
      previousState,
      assignmentStatusValues,
      isSaving,
      currentLanguage,
      users,
      workingUnits,
      orders,
      v$,
      ...useDateFormat({ entityRef: warehouseAssignment }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.warehouseAssignment.id) {
        this.warehouseAssignmentService()
          .update(this.warehouseAssignment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A WarehouseAssignment is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.warehouseAssignmentService()
          .create(this.warehouseAssignment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A WarehouseAssignment is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
