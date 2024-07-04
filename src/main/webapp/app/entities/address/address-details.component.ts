import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AddressService from './address.service';
import { useDateFormat } from '@/shared/composables';
import { type IAddress } from '@/shared/model/address.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AddressDetails',
  setup() {
    const dateFormat = useDateFormat();
    const addressService = inject('addressService', () => new AddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const address: Ref<IAddress> = ref({});

    const retrieveAddress = async addressId => {
      try {
        const res = await addressService().find(addressId);
        address.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.addressId) {
      retrieveAddress(route.params.addressId);
    }

    return {
      ...dateFormat,
      alertService,
      address,

      previousState,
    };
  },
});
