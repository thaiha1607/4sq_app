import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import UserDetailsService from './user-details.service';
import { type IUserDetails } from '@/shared/model/user-details.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserDetails',
  setup() {
    const dateFormat = useDateFormat();
    const userDetailsService = inject('userDetailsService', () => new UserDetailsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const userDetails: Ref<IUserDetails[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveUserDetailss = async () => {
      isFetching.value = true;
      try {
        const res = await userDetailsService().retrieve();
        userDetails.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveUserDetailss();
    };

    onMounted(async () => {
      await retrieveUserDetailss();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IUserDetails) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeUserDetails = async () => {
      try {
        await userDetailsService().delete(removeId.value);
        const message = 'A UserDetails is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveUserDetailss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      userDetails,
      handleSyncList,
      isFetching,
      retrieveUserDetailss,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeUserDetails,
    };
  },
});
