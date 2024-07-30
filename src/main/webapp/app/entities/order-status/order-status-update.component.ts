import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrderStatusService from './order-status.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IOrderStatus, OrderStatus } from '@/shared/model/order-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderStatusUpdate',
  setup() {
    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const orderStatus: Ref<IOrderStatus> = ref(new OrderStatus());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrderStatus = async orderStatusId => {
      try {
        const res = await orderStatusService().find(orderStatusId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        orderStatus.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderStatusId) {
      retrieveOrderStatus(route.params.orderStatusId);
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
    const v$ = useVuelidate(validationRules, orderStatus as any);
    v$.value.$validate();

    return {
      orderStatusService,
      alertService,
      orderStatus,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: orderStatus }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.orderStatus.id) {
        this.orderStatusService()
          .update(this.orderStatus)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A OrderStatus is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.orderStatusService()
          .create(this.orderStatus)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A OrderStatus is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
