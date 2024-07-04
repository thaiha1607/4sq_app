import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import UserAddressService from './user-address.service';
import { useDateFormat } from '@/shared/composables';
import { type IUserAddress } from '@/shared/model/user-address.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserAddressDetails',
  setup() {
    const dateFormat = useDateFormat();
    const userAddressService = inject('userAddressService', () => new UserAddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const userAddress: Ref<IUserAddress> = ref({});

    const retrieveUserAddress = async userAddressId => {
      try {
        const res = await userAddressService().find(userAddressId);
        userAddress.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userAddressId) {
      retrieveUserAddress(route.params.userAddressId);
    }

    return {
      ...dateFormat,
      alertService,
      userAddress,

      previousState,
    };
  },
});
