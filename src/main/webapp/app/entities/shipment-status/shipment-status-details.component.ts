import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ShipmentStatusService from './shipment-status.service';
import { useDateFormat } from '@/shared/composables';
import { type IShipmentStatus } from '@/shared/model/shipment-status.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentStatusDetails',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentStatusService = inject('shipmentStatusService', () => new ShipmentStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shipmentStatus: Ref<IShipmentStatus> = ref({});

    const retrieveShipmentStatus = async shipmentStatusStatusCode => {
      try {
        const res = await shipmentStatusService().find(shipmentStatusStatusCode);
        shipmentStatus.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentStatusId) {
      retrieveShipmentStatus(route.params.shipmentStatusId);
    }

    return {
      ...dateFormat,
      alertService,
      shipmentStatus,

      previousState,
    };
  },
});
