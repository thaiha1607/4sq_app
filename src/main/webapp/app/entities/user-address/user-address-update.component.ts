import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import UserAddressService from './user-address.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import AddressService from '@/entities/address/address.service';
import { type IAddress } from '@/shared/model/address.model';
import { type IUserAddress, UserAddress } from '@/shared/model/user-address.model';
import { AddressType } from '@/shared/model/enumerations/address-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserAddressUpdate',
  setup() {
    const userAddressService = inject('userAddressService', () => new UserAddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const userAddress: Ref<IUserAddress> = ref(new UserAddress());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const addressService = inject('addressService', () => new AddressService());

    const addresses: Ref<IAddress[]> = ref([]);
    const addressTypeValues: Ref<string[]> = ref(Object.keys(AddressType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveUserAddress = async userAddressId => {
      try {
        const res = await userAddressService().find(userAddressId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        userAddress.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userAddressId) {
      retrieveUserAddress(route.params.userAddressId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      addressService()
        .retrieve()
        .then(res => {
          addresses.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      type: {
        required: validations.required('This field is required.'),
      },
      friendlyName: {},
      isDefault: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {
        required: validations.required('This field is required.'),
      },
      address: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, userAddress as any);
    v$.value.$validate();

    return {
      userAddressService,
      alertService,
      userAddress,
      previousState,
      addressTypeValues,
      isSaving,
      currentLanguage,
      users,
      addresses,
      v$,
      ...useDateFormat({ entityRef: userAddress }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.userAddress.id) {
        this.userAddressService()
          .update(this.userAddress)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A UserAddress is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.userAddressService()
          .create(this.userAddress)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A UserAddress is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
