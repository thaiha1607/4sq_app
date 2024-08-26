import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InvoiceService from './invoice.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import InvoiceStatusService from '@/entities/invoice-status/invoice-status.service';
import { type IInvoiceStatus } from '@/shared/model/invoice-status.model';
import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import { type IInvoice, Invoice } from '@/shared/model/invoice.model';
import { InvoiceType } from '@/shared/model/enumerations/invoice-type.model';
import { PaymentMethod } from '@/shared/model/enumerations/payment-method.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InvoiceUpdate',
  setup() {
    const invoiceService = inject('invoiceService', () => new InvoiceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const invoice: Ref<IInvoice> = ref(new Invoice());

    const invoices: Ref<IInvoice[]> = ref([]);

    const invoiceStatusService = inject('invoiceStatusService', () => new InvoiceStatusService());

    const invoiceStatuses: Ref<IInvoiceStatus[]> = ref([]);

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);
    const invoiceTypeValues: Ref<string[]> = ref(Object.keys(InvoiceType));
    const paymentMethodValues: Ref<string[]> = ref(Object.keys(PaymentMethod));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInvoice = async invoiceId => {
      try {
        const res = await invoiceService().find(invoiceId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        invoice.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.invoiceId) {
      retrieveInvoice(route.params.invoiceId);
    }

    const initRelationships = () => {
      invoiceService()
        .retrieve()
        .then(res => {
          invoices.value = res.data;
        });
      invoiceStatusService()
        .retrieve()
        .then(res => {
          invoiceStatuses.value = res.data;
        });
      orderService()
        .retrieve()
        .then(res => {
          orders.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      totalAmount: {
        required: validations.required('This field is required.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      type: {
        required: validations.required('This field is required.'),
      },
      paymentMethod: {
        required: validations.required('This field is required.'),
      },
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      childInvoices: {},
      shipments: {},
      status: {
        required: validations.required('This field is required.'),
      },
      order: {
        required: validations.required('This field is required.'),
      },
      rootInvoice: {},
    };
    const v$ = useVuelidate(validationRules, invoice as any);
    v$.value.$validate();

    return {
      invoiceService,
      alertService,
      invoice,
      previousState,
      invoiceTypeValues,
      paymentMethodValues,
      isSaving,
      currentLanguage,
      invoices,
      invoiceStatuses,
      orders,
      v$,
      ...useDateFormat({ entityRef: invoice }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.invoice.id) {
        this.invoiceService()
          .update(this.invoice)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Invoice is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.invoiceService()
          .create(this.invoice)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Invoice is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
