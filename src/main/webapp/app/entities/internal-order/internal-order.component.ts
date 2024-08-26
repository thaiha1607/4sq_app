import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import InternalOrderService from './internal-order.service';
import { type IInternalOrder } from '@/shared/model/internal-order.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrder',
  setup() {
    const dateFormat = useDateFormat();
    const internalOrderService = inject('internalOrderService', () => new InternalOrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const internalOrders: Ref<IInternalOrder[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInternalOrders = async () => {
      isFetching.value = true;
      try {
        const res = await internalOrderService().retrieve();
        internalOrders.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInternalOrders();
    };

    onMounted(async () => {
      await retrieveInternalOrders();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInternalOrder) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInternalOrder = async () => {
      try {
        await internalOrderService().delete(removeId.value);
        const message = 'A InternalOrder is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInternalOrders();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      internalOrders,
      handleSyncList,
      isFetching,
      retrieveInternalOrders,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInternalOrder,
    };
  },
});
