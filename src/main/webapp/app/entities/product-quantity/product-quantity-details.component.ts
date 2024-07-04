import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ProductQuantityService from './product-quantity.service';
import { useDateFormat } from '@/shared/composables';
import { type IProductQuantity } from '@/shared/model/product-quantity.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductQuantityDetails',
  setup() {
    const dateFormat = useDateFormat();
    const productQuantityService = inject('productQuantityService', () => new ProductQuantityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const productQuantity: Ref<IProductQuantity> = ref({});

    const retrieveProductQuantity = async productQuantityId => {
      try {
        const res = await productQuantityService().find(productQuantityId);
        productQuantity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productQuantityId) {
      retrieveProductQuantity(route.params.productQuantityId);
    }

    return {
      ...dateFormat,
      alertService,
      productQuantity,

      previousState,
    };
  },
});
