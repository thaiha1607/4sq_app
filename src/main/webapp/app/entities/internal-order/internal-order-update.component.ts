import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InternalOrderService from './internal-order.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OrderStatusService from '@/entities/order-status/order-status.service';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import { type IInternalOrder, InternalOrder } from '@/shared/model/internal-order.model';
import { OrderType } from '@/shared/model/enumerations/order-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderUpdate',
  setup() {
    const internalOrderService = inject('internalOrderService', () => new InternalOrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const internalOrder: Ref<IInternalOrder> = ref(new InternalOrder());

    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());

    const orderStatuses: Ref<IOrderStatus[]> = ref([]);

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);
    const orderTypeValues: Ref<string[]> = ref(Object.keys(OrderType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInternalOrder = async internalOrderId => {
      try {
        const res = await internalOrderService().find(internalOrderId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        internalOrder.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.internalOrderId) {
      retrieveInternalOrder(route.params.internalOrderId);
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
      type: {
        required: validations.required('This field is required.'),
      },
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      histories: {},
      status: {
        required: validations.required('This field is required.'),
      },
      rootOrder: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, internalOrder as any);
    v$.value.$validate();

    return {
      internalOrderService,
      alertService,
      internalOrder,
      previousState,
      orderTypeValues,
      isSaving,
      currentLanguage,
      orderStatuses,
      orders,
      v$,
      ...useDateFormat({ entityRef: internalOrder }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.internalOrder.id) {
        this.internalOrderService()
          .update(this.internalOrder)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A InternalOrder is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.internalOrderService()
          .create(this.internalOrder)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A InternalOrder is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
