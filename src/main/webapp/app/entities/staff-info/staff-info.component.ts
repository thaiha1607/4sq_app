import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import StaffInfoService from './staff-info.service';
import { type IStaffInfo } from '@/shared/model/staff-info.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StaffInfo',
  setup() {
    const dateFormat = useDateFormat();
    const staffInfoService = inject('staffInfoService', () => new StaffInfoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const staffInfos: Ref<IStaffInfo[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveStaffInfos = async () => {
      isFetching.value = true;
      try {
        const res = await staffInfoService().retrieve();
        staffInfos.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveStaffInfos();
    };

    onMounted(async () => {
      await retrieveStaffInfos();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IStaffInfo) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeStaffInfo = async () => {
      try {
        await staffInfoService().delete(removeId.value);
        const message = 'A StaffInfo is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveStaffInfos();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      staffInfos,
      handleSyncList,
      isFetching,
      retrieveStaffInfos,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeStaffInfo,
    };
  },
});
