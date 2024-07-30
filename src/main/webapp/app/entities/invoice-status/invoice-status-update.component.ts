import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InvoiceStatusService from './invoice-status.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IInvoiceStatus, InvoiceStatus } from '@/shared/model/invoice-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InvoiceStatusUpdate',
  setup() {
    const invoiceStatusService = inject('invoiceStatusService', () => new InvoiceStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const invoiceStatus: Ref<IInvoiceStatus> = ref(new InvoiceStatus());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInvoiceStatus = async invoiceStatusId => {
      try {
        const res = await invoiceStatusService().find(invoiceStatusId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        invoiceStatus.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.invoiceStatusId) {
      retrieveInvoiceStatus(route.params.invoiceStatusId);
    }

    const validations = useValidation();
    const validationRules = {
      statusCode: {
        required: validations.required('This field is required.'),
      },
      description: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
    };
    const v$ = useVuelidate(validationRules, invoiceStatus as any);
    v$.value.$validate();

    return {
      invoiceStatusService,
      alertService,
      invoiceStatus,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: invoiceStatus }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.invoiceStatus.id) {
        this.invoiceStatusService()
          .update(this.invoiceStatus)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A InvoiceStatus is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.invoiceStatusService()
          .create(this.invoiceStatus)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A InvoiceStatus is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
