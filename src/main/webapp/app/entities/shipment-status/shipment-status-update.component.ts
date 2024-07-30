import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ShipmentStatusService from './shipment-status.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IShipmentStatus, ShipmentStatus } from '@/shared/model/shipment-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentStatusUpdate',
  setup() {
    const shipmentStatusService = inject('shipmentStatusService', () => new ShipmentStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipmentStatus: Ref<IShipmentStatus> = ref(new ShipmentStatus());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShipmentStatus = async shipmentStatusId => {
      try {
        const res = await shipmentStatusService().find(shipmentStatusId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        shipmentStatus.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentStatusId) {
      retrieveShipmentStatus(route.params.shipmentStatusId);
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
    const v$ = useVuelidate(validationRules, shipmentStatus as any);
    v$.value.$validate();

    return {
      shipmentStatusService,
      alertService,
      shipmentStatus,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: shipmentStatus }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shipmentStatus.id) {
        this.shipmentStatusService()
          .update(this.shipmentStatus)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ShipmentStatus is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shipmentStatusService()
          .create(this.shipmentStatus)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ShipmentStatus is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
