import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import InvoiceStatusService from './invoice-status.service';
import { type IInvoiceStatus } from '@/shared/model/invoice-status.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InvoiceStatus',
  setup() {
    const dateFormat = useDateFormat();
    const invoiceStatusService = inject('invoiceStatusService', () => new InvoiceStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const invoiceStatuses: Ref<IInvoiceStatus[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveInvoiceStatuss = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value
          ? await invoiceStatusService().search(currentSearch.value)
          : await invoiceStatusService().retrieve();
        invoiceStatuses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInvoiceStatuss();
    };

    onMounted(async () => {
      await retrieveInvoiceStatuss();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveInvoiceStatuss();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInvoiceStatus) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInvoiceStatus = async () => {
      try {
        await invoiceStatusService().delete(removeId.value);
        const message = 'A InvoiceStatus is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInvoiceStatuss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      invoiceStatuses,
      handleSyncList,
      isFetching,
      retrieveInvoiceStatuss,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInvoiceStatus,
    };
  },
});
