import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ShipmentService from './shipment.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ShipmentStatusService from '@/entities/shipment-status/shipment-status.service';
import { type IShipmentStatus } from '@/shared/model/shipment-status.model';
import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import InvoiceService from '@/entities/invoice/invoice.service';
import { type IInvoice } from '@/shared/model/invoice.model';
import { type IShipment, Shipment } from '@/shared/model/shipment.model';
import { ShipmentType } from '@/shared/model/enumerations/shipment-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentUpdate',
  setup() {
    const shipmentService = inject('shipmentService', () => new ShipmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipment: Ref<IShipment> = ref(new Shipment());

    const shipmentStatusService = inject('shipmentStatusService', () => new ShipmentStatusService());

    const shipmentStatuses: Ref<IShipmentStatus[]> = ref([]);

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);

    const invoiceService = inject('invoiceService', () => new InvoiceService());

    const invoices: Ref<IInvoice[]> = ref([]);
    const shipmentTypeValues: Ref<string[]> = ref(Object.keys(ShipmentType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShipment = async shipmentId => {
      try {
        const res = await shipmentService().find(shipmentId);
        res.shipmentDate = new Date(res.shipmentDate);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        shipment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentId) {
      retrieveShipment(route.params.shipmentId);
    }

    const initRelationships = () => {
      shipmentStatusService()
        .retrieve()
        .then(res => {
          shipmentStatuses.value = res.data;
        });
      orderService()
        .retrieve()
        .then(res => {
          orders.value = res.data;
        });
      invoiceService()
        .retrieve()
        .then(res => {
          invoices.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      type: {
        required: validations.required('This field is required.'),
      },
      shipmentDate: {
        required: validations.required('This field is required.'),
      },
      note: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      assignments: {},
      items: {},
      status: {
        required: validations.required('This field is required.'),
      },
      order: {
        required: validations.required('This field is required.'),
      },
      invoice: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, shipment as any);
    v$.value.$validate();

    return {
      shipmentService,
      alertService,
      shipment,
      previousState,
      shipmentTypeValues,
      isSaving,
      currentLanguage,
      shipmentStatuses,
      orders,
      invoices,
      v$,
      ...useDateFormat({ entityRef: shipment }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shipment.id) {
        this.shipmentService()
          .update(this.shipment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Shipment is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shipmentService()
          .create(this.shipment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Shipment is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
