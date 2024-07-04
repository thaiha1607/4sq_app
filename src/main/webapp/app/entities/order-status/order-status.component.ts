import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import OrderStatusService from './order-status.service';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderStatus',
  setup() {
    const dateFormat = useDateFormat();
    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const orderStatuses: Ref<IOrderStatus[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveOrderStatuss = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await orderStatusService().search(currentSearch.value) : await orderStatusService().retrieve();
        orderStatuses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveOrderStatuss();
    };

    onMounted(async () => {
      await retrieveOrderStatuss();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveOrderStatuss();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IOrderStatus) => {
      removeId.value = instance.statusCode;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeOrderStatus = async () => {
      try {
        await orderStatusService().delete(removeId.value);
        const message = 'A OrderStatus is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveOrderStatuss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      orderStatuses,
      handleSyncList,
      isFetching,
      retrieveOrderStatuss,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeOrderStatus,
    };
  },
});
