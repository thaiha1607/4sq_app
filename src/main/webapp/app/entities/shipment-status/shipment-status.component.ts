import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ShipmentStatusService from './shipment-status.service';
import { type IShipmentStatus } from '@/shared/model/shipment-status.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentStatus',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentStatusService = inject('shipmentStatusService', () => new ShipmentStatusService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const shipmentStatuses: Ref<IShipmentStatus[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveShipmentStatuss = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value
          ? await shipmentStatusService().search(currentSearch.value)
          : await shipmentStatusService().retrieve();
        shipmentStatuses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveShipmentStatuss();
    };

    onMounted(async () => {
      await retrieveShipmentStatuss();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveShipmentStatuss();
    };

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IShipmentStatus) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeShipmentStatus = async () => {
      try {
        await shipmentStatusService().delete(removeId.value);
        const message = 'A ShipmentStatus is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveShipmentStatuss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      shipmentStatuses,
      handleSyncList,
      isFetching,
      retrieveShipmentStatuss,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeShipmentStatus,
    };
  },
});
