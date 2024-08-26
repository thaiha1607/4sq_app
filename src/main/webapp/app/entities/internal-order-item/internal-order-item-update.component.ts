import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InternalOrderItemService from './internal-order-item.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OrderItemService from '@/entities/order-item/order-item.service';
import { type IOrderItem } from '@/shared/model/order-item.model';
import { type IInternalOrderItem, InternalOrderItem } from '@/shared/model/internal-order-item.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InternalOrderItemUpdate',
  setup() {
    const internalOrderItemService = inject('internalOrderItemService', () => new InternalOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const internalOrderItem: Ref<IInternalOrderItem> = ref(new InternalOrderItem());

    const orderItemService = inject('orderItemService', () => new OrderItemService());

    const orderItems: Ref<IOrderItem[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInternalOrderItem = async internalOrderItemId => {
      try {
        const res = await internalOrderItemService().find(internalOrderItemId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        internalOrderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.internalOrderItemId) {
      retrieveInternalOrderItem(route.params.internalOrderItemId);
    }

    const initRelationships = () => {
      orderItemService()
        .retrieve()
        .then(res => {
          orderItems.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      qty: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      orderItem: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, internalOrderItem as any);
    v$.value.$validate();

    return {
      internalOrderItemService,
      alertService,
      internalOrderItem,
      previousState,
      isSaving,
      currentLanguage,
      orderItems,
      v$,
      ...useDateFormat({ entityRef: internalOrderItem }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.internalOrderItem.id) {
        this.internalOrderItemService()
          .update(this.internalOrderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A InternalOrderItem is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.internalOrderItemService()
          .create(this.internalOrderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A InternalOrderItem is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
