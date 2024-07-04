import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import InvoiceStatusService from './invoice-status.service';
import { useDateFormat } from '@/shared/composables';
import { type IInvoiceStatus } from '@/shared/model/invoice-status.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InvoiceStatusDetails',
  setup() {
    const dateFormat = useDateFormat();
    const invoiceStatusService = inject('invoiceStatusService', () => new InvoiceStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const invoiceStatus: Ref<IInvoiceStatus> = ref({});

    const retrieveInvoiceStatus = async invoiceStatusStatusCode => {
      try {
        const res = await invoiceStatusService().find(invoiceStatusStatusCode);
        invoiceStatus.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.invoiceStatusId) {
      retrieveInvoiceStatus(route.params.invoiceStatusId);
    }

    return {
      ...dateFormat,
      alertService,
      invoiceStatus,

      previousState,
    };
  },
});
