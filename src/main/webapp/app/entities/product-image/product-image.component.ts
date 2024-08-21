import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ProductImageService from './product-image.service';
import { type IProductImage } from '@/shared/model/product-image.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductImage',
  setup() {
    const dateFormat = useDateFormat();
    const productImageService = inject('productImageService', () => new ProductImageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const productImages: Ref<IProductImage[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveProductImages = async () => {
      isFetching.value = true;
      try {
        const res = await productImageService().retrieve();
        productImages.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveProductImages();
    };

    onMounted(async () => {
      await retrieveProductImages();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IProductImage) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeProductImage = async () => {
      try {
        await productImageService().delete(removeId.value);
        const message = 'A ProductImage is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveProductImages();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      productImages,
      handleSyncList,
      isFetching,
      retrieveProductImages,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeProductImage,
    };
  },
});
