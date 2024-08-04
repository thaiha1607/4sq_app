import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import StaffInfoService from './staff-info.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import WorkingUnitService from '@/entities/working-unit/working-unit.service';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import { type IStaffInfo, StaffInfo } from '@/shared/model/staff-info.model';
import { StaffStatus } from '@/shared/model/enumerations/staff-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StaffInfoUpdate',
  setup() {
    const staffInfoService = inject('staffInfoService', () => new StaffInfoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const staffInfo: Ref<IStaffInfo> = ref(new StaffInfo());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());

    const workingUnits: Ref<IWorkingUnit[]> = ref([]);
    const staffStatusValues: Ref<string[]> = ref(Object.keys(StaffStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveStaffInfo = async staffInfoId => {
      try {
        const res = await staffInfoService().find(staffInfoId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        staffInfo.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.staffInfoId) {
      retrieveStaffInfo(route.params.staffInfoId);
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
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      status: {
        required: validations.required('This field is required.'),
      },
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {
        required: validations.required('This field is required.'),
      },
      workingUnit: {},
    };
    const v$ = useVuelidate(validationRules, staffInfo as any);
    v$.value.$validate();

    return {
      staffInfoService,
      alertService,
      staffInfo,
      previousState,
      staffStatusValues,
      isSaving,
      currentLanguage,
      users,
      workingUnits,
      v$,
      ...useDateFormat({ entityRef: staffInfo }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.staffInfo.id) {
        this.staffInfoService()
          .update(this.staffInfo)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A StaffInfo is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.staffInfoService()
          .create(this.staffInfo)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A StaffInfo is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
