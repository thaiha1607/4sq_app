import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ProductCategoryService from './product-category.service';
import { type IProductCategory } from '@/shared/model/product-category.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductCategory',
  setup() {
    const dateFormat = useDateFormat();
    const productCategoryService = inject('productCategoryService', () => new ProductCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const productCategories: Ref<IProductCategory[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveProductCategorys = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value
          ? await productCategoryService().search(currentSearch.value)
          : await productCategoryService().retrieve();
        productCategories.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveProductCategorys();
    };

    onMounted(async () => {
      await retrieveProductCategorys();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveProductCategorys();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IProductCategory) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeProductCategory = async () => {
      try {
        await productCategoryService().delete(removeId.value);
        const message = 'A ProductCategory is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveProductCategorys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      productCategories,
      handleSyncList,
      isFetching,
      retrieveProductCategorys,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeProductCategory,
    };
  },
});
