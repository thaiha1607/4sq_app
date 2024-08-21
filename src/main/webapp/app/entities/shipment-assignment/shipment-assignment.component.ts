import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ShipmentAssignmentService from './shipment-assignment.service';
import { type IShipmentAssignment } from '@/shared/model/shipment-assignment.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShipmentAssignment',
  setup() {
    const dateFormat = useDateFormat();
    const shipmentAssignmentService = inject('shipmentAssignmentService', () => new ShipmentAssignmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipmentAssignments: Ref<IShipmentAssignment[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveShipmentAssignments = async () => {
      isFetching.value = true;
      try {
        const res = await shipmentAssignmentService().retrieve();
        shipmentAssignments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveShipmentAssignments();
    };

    onMounted(async () => {
      await retrieveShipmentAssignments();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IShipmentAssignment) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeShipmentAssignment = async () => {
      try {
        await shipmentAssignmentService().delete(removeId.value);
        const message = 'A ShipmentAssignment is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveShipmentAssignments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      shipmentAssignments,
      handleSyncList,
      isFetching,
      retrieveShipmentAssignments,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeShipmentAssignment,
    };
  },
});
