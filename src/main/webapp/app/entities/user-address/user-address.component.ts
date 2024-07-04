import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import UserAddressService from './user-address.service';
import { type IUserAddress } from '@/shared/model/user-address.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserAddress',
  setup() {
    const dateFormat = useDateFormat();
    const userAddressService = inject('userAddressService', () => new UserAddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const userAddresses: Ref<IUserAddress[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveUserAddresss = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await userAddressService().search(currentSearch.value) : await userAddressService().retrieve();
        userAddresses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveUserAddresss();
    };

    onMounted(async () => {
      await retrieveUserAddresss();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveUserAddresss();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IUserAddress) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeUserAddress = async () => {
      try {
        await userAddressService().delete(removeId.value);
        const message = 'A UserAddress is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveUserAddresss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      userAddresses,
      handleSyncList,
      isFetching,
      retrieveUserAddresss,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeUserAddress,
    };
  },
});
