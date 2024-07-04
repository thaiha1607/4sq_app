import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrderItemService from './order-item.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ProductCategoryService from '@/entities/product-category/product-category.service';
import { type IProductCategory } from '@/shared/model/product-category.model';
import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import { type IOrderItem, OrderItem } from '@/shared/model/order-item.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderItemUpdate',
  setup() {
    const orderItemService = inject('orderItemService', () => new OrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const orderItem: Ref<IOrderItem> = ref(new OrderItem());

    const productCategoryService = inject('productCategoryService', () => new ProductCategoryService());

    const productCategories: Ref<IProductCategory[]> = ref([]);

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrderItem = async orderItemId => {
      try {
        const res = await orderItemService().find(orderItemId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        orderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderItemId) {
      retrieveOrderItem(route.params.orderItemId);
    }

    const initRelationships = () => {
      productCategoryService()
        .retrieve()
        .then(res => {
          productCategories.value = res.data;
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
      orderedQty: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      receivedQty: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      unitPrice: {
        required: validations.required('This field is required.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      productCategory: {
        required: validations.required('This field is required.'),
      },
      order: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, orderItem as any);
    v$.value.$validate();

    return {
      orderItemService,
      alertService,
      orderItem,
      previousState,
      isSaving,
      currentLanguage,
      productCategories,
      orders,
      v$,
      ...useDateFormat({ entityRef: orderItem }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.orderItem.id) {
        this.orderItemService()
          .update(this.orderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A OrderItem is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.orderItemService()
          .create(this.orderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A OrderItem is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
