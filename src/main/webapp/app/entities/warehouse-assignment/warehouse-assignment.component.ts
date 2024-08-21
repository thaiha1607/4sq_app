import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import WarehouseAssignmentService from './warehouse-assignment.service';
import { type IWarehouseAssignment } from '@/shared/model/warehouse-assignment.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'WarehouseAssignment',
  setup() {
    const dateFormat = useDateFormat();
    const warehouseAssignmentService = inject('warehouseAssignmentService', () => new WarehouseAssignmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const warehouseAssignments: Ref<IWarehouseAssignment[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveWarehouseAssignments = async () => {
      isFetching.value = true;
      try {
        const res = await warehouseAssignmentService().retrieve();
        warehouseAssignments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveWarehouseAssignments();
    };

    onMounted(async () => {
      await retrieveWarehouseAssignments();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IWarehouseAssignment) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeWarehouseAssignment = async () => {
      try {
        await warehouseAssignmentService().delete(removeId.value);
        const message = 'A WarehouseAssignment is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveWarehouseAssignments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      warehouseAssignments,
      handleSyncList,
      isFetching,
      retrieveWarehouseAssignments,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeWarehouseAssignment,
    };
  },
});
