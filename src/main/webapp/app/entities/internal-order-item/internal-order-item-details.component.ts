import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import InternalOrderItemService from './internal-order-item.service';
import { useDateFormat } from '@/shared/composables';
import { type IInternalOrderItem } from '@/shared/model/internal-order-item.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderItemDetails',
  setup() {
    const dateFormat = useDateFormat();
    const internalOrderItemService = inject('internalOrderItemService', () => new InternalOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const internalOrderItem: Ref<IInternalOrderItem> = ref({});

    const retrieveInternalOrderItem = async internalOrderItemId => {
      try {
        const res = await internalOrderItemService().find(internalOrderItemId);
        internalOrderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.internalOrderItemId) {
      retrieveInternalOrderItem(route.params.internalOrderItemId);
    }

    return {
      ...dateFormat,
      alertService,
      internalOrderItem,

      previousState,
    };
  },
});
