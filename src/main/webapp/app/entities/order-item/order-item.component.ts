import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import OrderItemService from './order-item.service';
import { type IOrderItem } from '@/shared/model/order-item.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderItem',
  setup() {
    const dateFormat = useDateFormat();
    const orderItemService = inject('orderItemService', () => new OrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const orderItems: Ref<IOrderItem[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveOrderItems = async () => {
      isFetching.value = true;
      try {
        const res = await orderItemService().retrieve();
        orderItems.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveOrderItems();
    };

    onMounted(async () => {
      await retrieveOrderItems();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IOrderItem) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeOrderItem = async () => {
      try {
        await orderItemService().delete(removeId.value);
        const message = 'A OrderItem is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveOrderItems();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      orderItems,
      handleSyncList,
      isFetching,
      retrieveOrderItems,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeOrderItem,
    };
  },
});
