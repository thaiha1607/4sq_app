import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ProductService from './product.service';
import { type IProduct } from '@/shared/model/product.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Product',
  setup() {
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const productService = inject('productService', () => new ProductService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const products: Ref<IProduct[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveProducts = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await productService().search(currentSearch.value) : await productService().retrieve();
        products.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveProducts();
    };

    onMounted(async () => {
      await retrieveProducts();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveProducts();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IProduct) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeProduct = async () => {
      try {
        await productService().delete(removeId.value);
        const message = 'A Product is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveProducts();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      products,
      handleSyncList,
      isFetching,
      retrieveProducts,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeProduct,
      ...dataUtils,
    };
  },
});
