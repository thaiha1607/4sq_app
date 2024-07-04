import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import OrderService from './order.service';
import { useDateFormat } from '@/shared/composables';
import { type IOrder } from '@/shared/model/order.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderDetails',
  setup() {
    const dateFormat = useDateFormat();
    const orderService = inject('orderService', () => new OrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const order: Ref<IOrder> = ref({});

    const retrieveOrder = async orderId => {
      try {
        const res = await orderService().find(orderId);
        order.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderId) {
      retrieveOrder(route.params.orderId);
    }

    return {
      ...dateFormat,
      alertService,
      order,

      previousState,
    };
  },
});
