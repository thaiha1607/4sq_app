import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrderHistoryService from './order-history.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OrderStatusService from '@/entities/order-status/order-status.service';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import { type IOrderHistory, OrderHistory } from '@/shared/model/order-history.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderHistoryUpdate',
  setup() {
    const orderHistoryService = inject('orderHistoryService', () => new OrderHistoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const orderHistory: Ref<IOrderHistory> = ref(new OrderHistory());

    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());

    const orderStatuses: Ref<IOrderStatus[]> = ref([]);

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrderHistory = async orderHistoryId => {
      try {
        const res = await orderHistoryService().find(orderHistoryId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        orderHistory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderHistoryId) {
      retrieveOrderHistory(route.params.orderHistoryId);
    }

    const initRelationships = () => {
      orderStatusService()
        .retrieve()
        .then(res => {
          orderStatuses.value = res.data;
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
    const v$ = useVuelidate(validationRules, orderHistory as any);
    v$.value.$validate();

    return {
      orderHistoryService,
      alertService,
      orderHistory,
      previousState,
      isSaving,
      currentLanguage,
      orderStatuses,
      orders,
      v$,
      ...useDateFormat({ entityRef: orderHistory }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.orderHistory.id) {
        this.orderHistoryService()
          .update(this.orderHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A OrderHistory is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.orderHistoryService()
          .create(this.orderHistory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A OrderHistory is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
