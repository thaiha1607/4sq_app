import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import OrderItemService from './order-item.service';
import { useDateFormat } from '@/shared/composables';
import { type IOrderItem } from '@/shared/model/order-item.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderItemDetails',
  setup() {
    const dateFormat = useDateFormat();
    const orderItemService = inject('orderItemService', () => new OrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const orderItem: Ref<IOrderItem> = ref({});

    const retrieveOrderItem = async orderItemId => {
      try {
        const res = await orderItemService().find(orderItemId);
        orderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderItemId) {
      retrieveOrderItem(route.params.orderItemId);
    }

    return {
      ...dateFormat,
      alertService,
      orderItem,

      previousState,
    };
  },
});
