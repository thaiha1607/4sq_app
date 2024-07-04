import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import UserDetailsService from './user-details.service';
import { useDateFormat } from '@/shared/composables';
import { type IUserDetails } from '@/shared/model/user-details.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserDetailsDetails',
  setup() {
    const dateFormat = useDateFormat();
    const userDetailsService = inject('userDetailsService', () => new UserDetailsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const userDetails: Ref<IUserDetails> = ref({});

    const retrieveUserDetails = async userDetailsId => {
      try {
        const res = await userDetailsService().find(userDetailsId);
        userDetails.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userDetailsId) {
      retrieveUserDetails(route.params.userDetailsId);
    }

    return {
      ...dateFormat,
      alertService,
      userDetails,

      previousState,
    };
  },
});
