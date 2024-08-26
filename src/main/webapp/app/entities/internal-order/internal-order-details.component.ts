import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import InternalOrderService from './internal-order.service';
import { useDateFormat } from '@/shared/composables';
import { type IInternalOrder } from '@/shared/model/internal-order.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderDetails',
  setup() {
    const dateFormat = useDateFormat();
    const internalOrderService = inject('internalOrderService', () => new InternalOrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const internalOrder: Ref<IInternalOrder> = ref({});

    const retrieveInternalOrder = async internalOrderId => {
      try {
        const res = await internalOrderService().find(internalOrderId);
        internalOrder.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.internalOrderId) {
      retrieveInternalOrder(route.params.internalOrderId);
    }

    return {
      ...dateFormat,
      alertService,
      internalOrder,

      previousState,
    };
  },
});
