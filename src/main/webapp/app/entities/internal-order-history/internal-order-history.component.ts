import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import InternalOrderHistoryService from './internal-order-history.service';
import { type IInternalOrderHistory } from '@/shared/model/internal-order-history.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderHistory',
  setup() {
    const dateFormat = useDateFormat();
    const internalOrderHistoryService = inject('internalOrderHistoryService', () => new InternalOrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const internalOrderHistories: Ref<IInternalOrderHistory[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInternalOrderHistorys = async () => {
      isFetching.value = true;
      try {
        const res = await internalOrderHistoryService().retrieve();
        internalOrderHistories.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInternalOrderHistorys();
    };

    onMounted(async () => {
      await retrieveInternalOrderHistorys();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInternalOrderHistory) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInternalOrderHistory = async () => {
      try {
        await internalOrderHistoryService().delete(removeId.value);
        const message = 'A InternalOrderHistory is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInternalOrderHistorys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      internalOrderHistories,
      handleSyncList,
      isFetching,
      retrieveInternalOrderHistorys,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInternalOrderHistory,
    };
  },
});
