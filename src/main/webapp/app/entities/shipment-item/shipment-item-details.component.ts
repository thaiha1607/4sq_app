import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ShipmentItemService from './shipment-item.service';
import { useDateFormat } from '@/shared/composables';
import { type IShipmentItem } from '@/shared/model/shipment-item.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentItemDetails',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentItemService = inject('shipmentItemService', () => new ShipmentItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shipmentItem: Ref<IShipmentItem> = ref({});

    const retrieveShipmentItem = async shipmentItemId => {
      try {
        const res = await shipmentItemService().find(shipmentItemId);
        shipmentItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentItemId) {
      retrieveShipmentItem(route.params.shipmentItemId);
    }

    return {
      ...dateFormat,
      alertService,
      shipmentItem,

      previousState,
    };
  },
});
