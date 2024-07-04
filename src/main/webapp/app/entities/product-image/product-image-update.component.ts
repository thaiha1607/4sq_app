import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ProductImageService from './product-image.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ProductService from '@/entities/product/product.service';
import { type IProduct } from '@/shared/model/product.model';
import { type IProductImage, ProductImage } from '@/shared/model/product-image.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductImageUpdate',
  setup() {
    const productImageService = inject('productImageService', () => new ProductImageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const productImage: Ref<IProductImage> = ref(new ProductImage());

    const productService = inject('productService', () => new ProductService());

    const products: Ref<IProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProductImage = async productImageId => {
      try {
        const res = await productImageService().find(productImageId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        productImage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productImageId) {
      retrieveProductImage(route.params.productImageId);
    }

    const initRelationships = () => {
      productService()
        .retrieve()
        .then(res => {
          products.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      imageUri: {
        required: validations.required('This field is required.'),
      },
      altText: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      product: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, productImage as any);
    v$.value.$validate();

    return {
      productImageService,
      alertService,
      productImage,
      previousState,
      isSaving,
      currentLanguage,
      products,
      v$,
      ...useDateFormat({ entityRef: productImage }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.productImage.id) {
        this.productImageService()
          .update(this.productImage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ProductImage is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.productImageService()
          .create(this.productImage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ProductImage is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
