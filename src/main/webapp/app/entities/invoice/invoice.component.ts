import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import InvoiceService from './invoice.service';
import { type IInvoice } from '@/shared/model/invoice.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Invoice',
  setup() {
    const dateFormat = useDateFormat();
    const invoiceService = inject('invoiceService', () => new InvoiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const invoices: Ref<IInvoice[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveInvoices = async () => {
      isFetching.value = true;
      try {
        const res = await invoiceService().retrieve();
        invoices.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveInvoices();
    };

    onMounted(async () => {
      await retrieveInvoices();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IInvoice) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeInvoice = async () => {
      try {
        await invoiceService().delete(removeId.value);
        const message = 'A Invoice is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveInvoices();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      invoices,
      handleSyncList,
      isFetching,
      retrieveInvoices,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeInvoice,
    };
  },
});
