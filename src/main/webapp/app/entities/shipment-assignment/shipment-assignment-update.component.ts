import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ShipmentAssignmentService from './shipment-assignment.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import ShipmentService from '@/entities/shipment/shipment.service';
import { type IShipment } from '@/shared/model/shipment.model';
import { type IShipmentAssignment, ShipmentAssignment } from '@/shared/model/shipment-assignment.model';
import { AssignmentStatus } from '@/shared/model/enumerations/assignment-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentAssignmentUpdate',
  setup() {
    const shipmentAssignmentService = inject('shipmentAssignmentService', () => new ShipmentAssignmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipmentAssignment: Ref<IShipmentAssignment> = ref(new ShipmentAssignment());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const shipmentService = inject('shipmentService', () => new ShipmentService());

    const shipments: Ref<IShipment[]> = ref([]);
    const assignmentStatusValues: Ref<string[]> = ref(Object.keys(AssignmentStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShipmentAssignment = async shipmentAssignmentId => {
      try {
        const res = await shipmentAssignmentService().find(shipmentAssignmentId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        shipmentAssignment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentAssignmentId) {
      retrieveShipmentAssignment(route.params.shipmentAssignmentId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      shipmentService()
        .retrieve()
        .then(res => {
          shipments.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      status: {
        required: validations.required('This field is required.'),
      },
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {},
      shipment: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, shipmentAssignment as any);
    v$.value.$validate();

    return {
      shipmentAssignmentService,
      alertService,
      shipmentAssignment,
      previousState,
      assignmentStatusValues,
      isSaving,
      currentLanguage,
      users,
      shipments,
      v$,
      ...useDateFormat({ entityRef: shipmentAssignment }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shipmentAssignment.id) {
        this.shipmentAssignmentService()
          .update(this.shipmentAssignment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ShipmentAssignment is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shipmentAssignmentService()
          .create(this.shipmentAssignment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ShipmentAssignment is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
