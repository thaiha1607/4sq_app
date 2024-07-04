import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import OrderService from './order.service';
import { type IOrder } from '@/shared/model/order.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Order',
  setup() {
    const dateFormat = useDateFormat();
    const orderService = inject('orderService', () => new OrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const orders: Ref<IOrder[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveOrders = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await orderService().search(currentSearch.value) : await orderService().retrieve();
        orders.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveOrders();
    };

    onMounted(async () => {
      await retrieveOrders();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveOrders();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IOrder) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeOrder = async () => {
      try {
        await orderService().delete(removeId.value);
        const message = 'A Order is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveOrders();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      orders,
      handleSyncList,
      isFetching,
      retrieveOrders,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeOrder,
    };
  },
});
