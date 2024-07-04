import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import AddressService from './address.service';
import { type IAddress } from '@/shared/model/address.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Address',
  setup() {
    const dateFormat = useDateFormat();
    const addressService = inject('addressService', () => new AddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const addresses: Ref<IAddress[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveAddresss = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await addressService().search(currentSearch.value) : await addressService().retrieve();
        addresses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveAddresss();
    };

    onMounted(async () => {
      await retrieveAddresss();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveAddresss();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IAddress) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeAddress = async () => {
      try {
        await addressService().delete(removeId.value);
        const message = 'A Address is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveAddresss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      addresses,
      handleSyncList,
      isFetching,
      retrieveAddresss,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeAddress,
    };
  },
});
