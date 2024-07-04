import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ProductImageService from './product-image.service';
import { useDateFormat } from '@/shared/composables';
import { type IProductImage } from '@/shared/model/product-image.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductImageDetails',
  setup() {
    const dateFormat = useDateFormat();
    const productImageService = inject('productImageService', () => new ProductImageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const productImage: Ref<IProductImage> = ref({});

    const retrieveProductImage = async productImageId => {
      try {
        const res = await productImageService().find(productImageId);
        productImage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productImageId) {
      retrieveProductImage(route.params.productImageId);
    }

    return {
      ...dateFormat,
      alertService,
      productImage,

      previousState,
    };
  },
});
