import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import AddressService from './address.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IAddress, Address } from '@/shared/model/address.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AddressUpdate',
  setup() {
    const addressService = inject('addressService', () => new AddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const address: Ref<IAddress> = ref(new Address());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveAddress = async addressId => {
      try {
        const res = await addressService().find(addressId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        address.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.addressId) {
      retrieveAddress(route.params.addressId);
    }

    const validations = useValidation();
    const validationRules = {
      line1: {
        required: validations.required('This field is required.'),
      },
      line2: {},
      city: {
        required: validations.required('This field is required.'),
      },
      state: {
        required: validations.required('This field is required.'),
      },
      country: {
        required: validations.required('This field is required.'),
      },
      zipOrPostalCode: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
    };
    const v$ = useVuelidate(validationRules, address as any);
    v$.value.$validate();

    return {
      addressService,
      alertService,
      address,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: address }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.address.id) {
        this.addressService()
          .update(this.address)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Address is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.addressService()
          .create(this.address)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Address is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
