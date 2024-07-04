import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import OrderStatusService from './order-status.service';
import { useDateFormat } from '@/shared/composables';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderStatusDetails',
  setup() {
    const dateFormat = useDateFormat();
    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const orderStatus: Ref<IOrderStatus> = ref({});

    const retrieveOrderStatus = async orderStatusStatusCode => {
      try {
        const res = await orderStatusService().find(orderStatusStatusCode);
        orderStatus.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderStatusId) {
      retrieveOrderStatus(route.params.orderStatusId);
    }

    return {
      ...dateFormat,
      alertService,
      orderStatus,

      previousState,
    };
  },
});
