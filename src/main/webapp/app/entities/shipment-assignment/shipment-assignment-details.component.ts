import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ShipmentAssignmentService from './shipment-assignment.service';
import { useDateFormat } from '@/shared/composables';
import { type IShipmentAssignment } from '@/shared/model/shipment-assignment.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentAssignmentDetails',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentAssignmentService = inject('shipmentAssignmentService', () => new ShipmentAssignmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shipmentAssignment: Ref<IShipmentAssignment> = ref({});

    const retrieveShipmentAssignment = async shipmentAssignmentId => {
      try {
        const res = await shipmentAssignmentService().find(shipmentAssignmentId);
        shipmentAssignment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentAssignmentId) {
      retrieveShipmentAssignment(route.params.shipmentAssignmentId);
    }

    return {
      ...dateFormat,
      alertService,
      shipmentAssignment,

      previousState,
    };
  },
});
