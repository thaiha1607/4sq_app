import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ShipmentItemService from './shipment-item.service';
import { type IShipmentItem } from '@/shared/model/shipment-item.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentItem',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentItemService = inject('shipmentItemService', () => new ShipmentItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const shipmentItems: Ref<IShipmentItem[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveShipmentItems = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await shipmentItemService().search(currentSearch.value) : await shipmentItemService().retrieve();
        shipmentItems.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveShipmentItems();
    };

    onMounted(async () => {
      await retrieveShipmentItems();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveShipmentItems();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IShipmentItem) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeShipmentItem = async () => {
      try {
        await shipmentItemService().delete(removeId.value);
        const message = 'A ShipmentItem is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveShipmentItems();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      shipmentItems,
      handleSyncList,
      isFetching,
      retrieveShipmentItems,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeShipmentItem,
    };
  },
});
