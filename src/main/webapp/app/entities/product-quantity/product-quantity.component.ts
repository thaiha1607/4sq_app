import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ProductQuantityService from './product-quantity.service';
import { type IProductQuantity } from '@/shared/model/product-quantity.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductQuantity',
  setup() {
    const dateFormat = useDateFormat();
    const productQuantityService = inject('productQuantityService', () => new ProductQuantityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const productQuantities: Ref<IProductQuantity[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveProductQuantitys = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value
          ? await productQuantityService().search(currentSearch.value)
          : await productQuantityService().retrieve();
        productQuantities.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveProductQuantitys();
    };

    onMounted(async () => {
      await retrieveProductQuantitys();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveProductQuantitys();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IProductQuantity) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeProductQuantity = async () => {
      try {
        await productQuantityService().delete(removeId.value);
        const message = 'A ProductQuantity is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveProductQuantitys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      productQuantities,
      handleSyncList,
      isFetching,
      retrieveProductQuantitys,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeProductQuantity,
    };
  },
});
