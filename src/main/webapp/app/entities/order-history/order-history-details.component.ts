import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import OrderHistoryService from './order-history.service';
import { useDateFormat } from '@/shared/composables';
import { type IOrderHistory } from '@/shared/model/order-history.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderHistoryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const orderHistoryService = inject('orderHistoryService', () => new OrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const orderHistory: Ref<IOrderHistory> = ref({});

    const retrieveOrderHistory = async orderHistoryId => {
      try {
        const res = await orderHistoryService().find(orderHistoryId);
        orderHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderHistoryId) {
      retrieveOrderHistory(route.params.orderHistoryId);
    }

    return {
      ...dateFormat,
      alertService,
      orderHistory,

      previousState,
    };
  },
});
