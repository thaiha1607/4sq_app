import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrderService from './order.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import OrderStatusService from '@/entities/order-status/order-status.service';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import AddressService from '@/entities/address/address.service';
import { type IAddress } from '@/shared/model/address.model';
import { type IOrder, Order } from '@/shared/model/order.model';
import { OrderType } from '@/shared/model/enumerations/order-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderUpdate',
  setup() {
    const orderService = inject('orderService', () => new OrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const order: Ref<IOrder> = ref(new Order());

    const orders: Ref<IOrder[]> = ref([]);
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const orderStatusService = inject('orderStatusService', () => new OrderStatusService());

    const orderStatuses: Ref<IOrderStatus[]> = ref([]);

    const addressService = inject('addressService', () => new AddressService());

    const addresses: Ref<IAddress[]> = ref([]);
    const orderTypeValues: Ref<string[]> = ref(Object.keys(OrderType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrder = async orderId => {
      try {
        const res = await orderService().find(orderId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        order.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderId) {
      retrieveOrder(route.params.orderId);
    }

    const initRelationships = () => {
      orderService()
        .retrieve()
        .then(res => {
          orders.value = res.data;
        });
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      orderStatusService()
        .retrieve()
        .then(res => {
          orderStatuses.value = res.data;
        });
      addressService()
        .retrieve()
        .then(res => {
          addresses.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      type: {
        required: validations.required('This field is required.'),
      },
      priority: {
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 0.', 0),
        max: validations.maxValue('This field cannot be more than 100.', 100),
      },
      isInternal: {},
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      creator: {
        required: validations.required('This field is required.'),
      },
      customer: {},
      status: {
        required: validations.required('This field is required.'),
      },
      address: {},
      parentOrder: {},
    };
    const v$ = useVuelidate(validationRules, order as any);
    v$.value.$validate();

    return {
      orderService,
      alertService,
      order,
      previousState,
      orderTypeValues,
      isSaving,
      currentLanguage,
      orders,
      users,
      orderStatuses,
      addresses,
      v$,
      ...useDateFormat({ entityRef: order }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.order.id) {
        this.orderService()
          .update(this.order)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Order is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.orderService()
          .create(this.order)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Order is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
