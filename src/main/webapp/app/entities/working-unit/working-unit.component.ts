import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import WorkingUnitService from './working-unit.service';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'WorkingUnit',
  setup() {
    const dateFormat = useDateFormat();
    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const workingUnits: Ref<IWorkingUnit[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveWorkingUnits = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await workingUnitService().search(currentSearch.value) : await workingUnitService().retrieve();
        workingUnits.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveWorkingUnits();
    };

    onMounted(async () => {
      await retrieveWorkingUnits();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveWorkingUnits();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IWorkingUnit) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeWorkingUnit = async () => {
      try {
        await workingUnitService().delete(removeId.value);
        const message = 'A WorkingUnit is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveWorkingUnits();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      workingUnits,
      handleSyncList,
      isFetching,
      retrieveWorkingUnits,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeWorkingUnit,
    };
  },
});
