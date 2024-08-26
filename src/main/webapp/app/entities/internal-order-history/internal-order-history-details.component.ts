import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import InternalOrderHistoryService from './internal-order-history.service';
import { useDateFormat } from '@/shared/composables';
import { type IInternalOrderHistory } from '@/shared/model/internal-order-history.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderHistoryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const internalOrderHistoryService = inject('internalOrderHistoryService', () => new InternalOrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const internalOrderHistory: Ref<IInternalOrderHistory> = ref({});

    const retrieveInternalOrderHistory = async internalOrderHistoryId => {
      try {
        const res = await internalOrderHistoryService().find(internalOrderHistoryId);
        internalOrderHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.internalOrderHistoryId) {
      retrieveInternalOrderHistory(route.params.internalOrderHistoryId);
    }

    return {
      ...dateFormat,
      alertService,
      internalOrderHistory,

      previousState,
    };
  },
});
