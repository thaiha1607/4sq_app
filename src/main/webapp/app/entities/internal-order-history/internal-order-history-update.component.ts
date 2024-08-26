import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InternalOrderHistoryService from './internal-order-history.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OrderStatusService from '@/entities/order-status/order-status.service';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import InternalOrderService from '@/entities/internal-order/internal-order.service';
import { type IInternalOrder } from '@/shared/model/internal-order.model';
import { type IInternalOrderHistory, InternalOrderHistory } from '@/shared/model/internal-order-history.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderHistoryUpdate',
  setup() {
    const internalOrderHistoryService = inject('internalOrderHistoryService', () => new InternalOrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const internalOrderHistory: Ref<IInternalOrderHistory> = ref(new InternalOrderHistory());

    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());

    const orderStatuses: Ref<IOrderStatus[]> = ref([]);

    const internalOrderService = inject('internalOrderService', () => new InternalOrderService());

    const internalOrders: Ref<IInternalOrder[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInternalOrderHistory = async internalOrderHistoryId => {
      try {
        const res = await internalOrderHistoryService().find(internalOrderHistoryId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        internalOrderHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.internalOrderHistoryId) {
      retrieveInternalOrderHistory(route.params.internalOrderHistoryId);
    }

    const initRelationships = () => {
      orderStatusService()
        .retrieve()
        .then(res => {
          orderStatuses.value = res.data;
        });
      internalOrderService()
        .retrieve()
        .then(res => {
          internalOrders.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      status: {
        required: validations.required('This field is required.'),
      },
      order: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, internalOrderHistory as any);
    v$.value.$validate();

    return {
      internalOrderHistoryService,
      alertService,
      internalOrderHistory,
      previousState,
      isSaving,
      currentLanguage,
      orderStatuses,
      internalOrders,
      v$,
      ...useDateFormat({ entityRef: internalOrderHistory }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.internalOrderHistory.id) {
        this.internalOrderHistoryService()
          .update(this.internalOrderHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A InternalOrderHistory is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.internalOrderHistoryService()
          .create(this.internalOrderHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A InternalOrderHistory is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
