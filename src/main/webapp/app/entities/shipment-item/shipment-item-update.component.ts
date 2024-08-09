import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ShipmentItemService from './shipment-item.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OrderItemService from '@/entities/order-item/order-item.service';
import { type IOrderItem } from '@/shared/model/order-item.model';
import ShipmentService from '@/entities/shipment/shipment.service';
import { type IShipment } from '@/shared/model/shipment.model';
import { type IShipmentItem, ShipmentItem } from '@/shared/model/shipment-item.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentItemUpdate',
  setup() {
    const shipmentItemService = inject('shipmentItemService', () => new ShipmentItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipmentItem: Ref<IShipmentItem> = ref(new ShipmentItem());

    const orderItemService = inject('orderItemService', () => new OrderItemService());

    const orderItems: Ref<IOrderItem[]> = ref([]);

    const shipmentService = inject('shipmentService', () => new ShipmentService());

    const shipments: Ref<IShipment[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShipmentItem = async shipmentItemId => {
      try {
        const res = await shipmentItemService().find(shipmentItemId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        shipmentItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentItemId) {
      retrieveShipmentItem(route.params.shipmentItemId);
    }

    const initRelationships = () => {
      orderItemService()
        .retrieve()
        .then(res => {
          orderItems.value = res.data;
        });
      shipmentService()
        .retrieve()
        .then(res => {
          shipments.value = res.data;
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
      total: {
        required: validations.required('This field is required.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      rollQty: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      orderItem: {
        required: validations.required('This field is required.'),
      },
      shipment: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, shipmentItem as any);
    v$.value.$validate();

    return {
      shipmentItemService,
      alertService,
      shipmentItem,
      previousState,
      isSaving,
      currentLanguage,
      orderItems,
      shipments,
      v$,
      ...useDateFormat({ entityRef: shipmentItem }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shipmentItem.id) {
        this.shipmentItemService()
          .update(this.shipmentItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ShipmentItem is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shipmentItemService()
          .create(this.shipmentItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ShipmentItem is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
