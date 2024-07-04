import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ProductCategoryService from './product-category.service';
import { useDateFormat } from '@/shared/composables';
import { type IProductCategory } from '@/shared/model/product-category.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductCategoryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const productCategoryService = inject('productCategoryService', () => new ProductCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const productCategory: Ref<IProductCategory> = ref({});

    const retrieveProductCategory = async productCategoryId => {
      try {
        const res = await productCategoryService().find(productCategoryId);
        productCategory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productCategoryId) {
      retrieveProductCategory(route.params.productCategoryId);
    }

    return {
      ...dateFormat,
      alertService,
      productCategory,

      previousState,
    };
  },
});
