import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import OrderHistoryService from './order-history.service';
import { type IOrderHistory } from '@/shared/model/order-history.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderHistory',
  setup() {
    const dateFormat = useDateFormat();
    const orderHistoryService = inject('orderHistoryService', () => new OrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const orderHistories: Ref<IOrderHistory[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveOrderHistorys = async () => {
      isFetching.value = true;
      try {
        const res = await orderHistoryService().retrieve();
        orderHistories.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveOrderHistorys();
    };

    onMounted(async () => {
      await retrieveOrderHistorys();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IOrderHistory) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeOrderHistory = async () => {
      try {
        await orderHistoryService().delete(removeId.value);
        const message = 'A OrderHistory is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveOrderHistorys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      orderHistories,
      handleSyncList,
      isFetching,
      retrieveOrderHistorys,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeOrderHistory,
    };
  },
});
