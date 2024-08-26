import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import InternalOrderItemService from './internal-order-item.service';
import { type IInternalOrderItem } from '@/shared/model/internal-order-item.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderItem',
  setup() {
    const dateFormat = useDateFormat();
    const internalOrderItemService = inject('internalOrderItemService', () => new InternalOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const internalOrderItems: Ref<IInternalOrderItem[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInternalOrderItems = async () => {
      isFetching.value = true;
      try {
        const res = await internalOrderItemService().retrieve();
        internalOrderItems.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInternalOrderItems();
    };

    onMounted(async () => {
      await retrieveInternalOrderItems();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInternalOrderItem) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInternalOrderItem = async () => {
      try {
        await internalOrderItemService().delete(removeId.value);
        const message = 'A InternalOrderItem is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInternalOrderItems();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      internalOrderItems,
      handleSyncList,
      isFetching,
      retrieveInternalOrderItems,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInternalOrderItem,
    };
  },
});
