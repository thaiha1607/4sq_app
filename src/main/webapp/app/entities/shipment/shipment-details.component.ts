import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ShipmentService from './shipment.service';
import { useDateFormat } from '@/shared/composables';
import { type IShipment } from '@/shared/model/shipment.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentDetails',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentService = inject('shipmentService', () => new ShipmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shipment: Ref<IShipment> = ref({});

    const retrieveShipment = async shipmentId => {
      try {
        const res = await shipmentService().find(shipmentId);
        shipment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentId) {
      retrieveShipment(route.params.shipmentId);
    }

    return {
      ...dateFormat,
      alertService,
      shipment,

      previousState,
    };
  },
});
