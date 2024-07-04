import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import WorkingUnitService from './working-unit.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import AddressService from '@/entities/address/address.service';
import { type IAddress } from '@/shared/model/address.model';
import { type IWorkingUnit, WorkingUnit } from '@/shared/model/working-unit.model';
import { WorkingUnitType } from '@/shared/model/enumerations/working-unit-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'WorkingUnitUpdate',
  setup() {
    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const workingUnit: Ref<IWorkingUnit> = ref(new WorkingUnit());

    const addressService = inject('addressService', () => new AddressService());

    const addresses: Ref<IAddress[]> = ref([]);
    const workingUnitTypeValues: Ref<string[]> = ref(Object.keys(WorkingUnitType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveWorkingUnit = async workingUnitId => {
      try {
        const res = await workingUnitService().find(workingUnitId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        workingUnit.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.workingUnitId) {
      retrieveWorkingUnit(route.params.workingUnitId);
    }

    const initRelationships = () => {
      addressService()
        .retrieve()
        .then(res => {
          addresses.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('This field is required.'),
      },
      type: {
        required: validations.required('This field is required.'),
      },
      imageUri: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      address: {},
    };
    const v$ = useVuelidate(validationRules, workingUnit as any);
    v$.value.$validate();

    return {
      workingUnitService,
      alertService,
      workingUnit,
      previousState,
      workingUnitTypeValues,
      isSaving,
      currentLanguage,
      addresses,
      v$,
      ...useDateFormat({ entityRef: workingUnit }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.workingUnit.id) {
        this.workingUnitService()
          .update(this.workingUnit)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A WorkingUnit is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.workingUnitService()
          .create(this.workingUnit)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A WorkingUnit is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
