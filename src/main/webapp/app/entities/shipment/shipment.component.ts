import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ShipmentService from './shipment.service';
import { type IShipment } from '@/shared/model/shipment.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Shipment',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentService = inject('shipmentService', () => new ShipmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipments: Ref<IShipment[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveShipments = async () => {
      isFetching.value = true;
      try {
        const res = await shipmentService().retrieve();
        shipments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveShipments();
    };

    onMounted(async () => {
      await retrieveShipments();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IShipment) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeShipment = async () => {
      try {
        await shipmentService().delete(removeId.value);
        const message = 'A Shipment is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveShipments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      shipments,
      handleSyncList,
      isFetching,
      retrieveShipments,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeShipment,
    };
  },
});
