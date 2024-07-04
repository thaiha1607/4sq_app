import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import InvoiceService from './invoice.service';
import { useDateFormat } from '@/shared/composables';
import { type IInvoice } from '@/shared/model/invoice.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InvoiceDetails',
  setup() {
    const dateFormat = useDateFormat();
    const invoiceService = inject('invoiceService', () => new InvoiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const invoice: Ref<IInvoice> = ref({});

    const retrieveInvoice = async invoiceId => {
      try {
        const res = await invoiceService().find(invoiceId);
        invoice.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.invoiceId) {
      retrieveInvoice(route.params.invoiceId);
    }

    return {
      ...dateFormat,
      alertService,
      invoice,

      previousState,
    };
  },
});
