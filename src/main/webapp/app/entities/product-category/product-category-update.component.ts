import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ProductCategoryService from './product-category.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ColourService from '@/entities/colour/colour.service';
import { type IColour } from '@/shared/model/colour.model';
import ProductService from '@/entities/product/product.service';
import { type IProduct } from '@/shared/model/product.model';
import { type IProductCategory, ProductCategory } from '@/shared/model/product-category.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductCategoryUpdate',
  setup() {
    const productCategoryService = inject('productCategoryService', () => new ProductCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const productCategory: Ref<IProductCategory> = ref(new ProductCategory());

    const colourService = inject('colourService', () => new ColourService());

    const colours: Ref<IColour[]> = ref([]);

    const productService = inject('productService', () => new ProductService());

    const products: Ref<IProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProductCategory = async productCategoryId => {
      try {
        const res = await productCategoryService().find(productCategoryId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        productCategory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productCategoryId) {
      retrieveProductCategory(route.params.productCategoryId);
    }

    const initRelationships = () => {
      colourService()
        .retrieve()
        .then(res => {
          colours.value = res.data;
        });
      productService()
        .retrieve()
        .then(res => {
          products.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      colour: {
        required: validations.required('This field is required.'),
      },
      product: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, productCategory as any);
    v$.value.$validate();

    return {
      productCategoryService,
      alertService,
      productCategory,
      previousState,
      isSaving,
      currentLanguage,
      colours,
      products,
      v$,
      ...useDateFormat({ entityRef: productCategory }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.productCategory.id) {
        this.productCategoryService()
          .update(this.productCategory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ProductCategory is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.productCategoryService()
          .create(this.productCategory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ProductCategory is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
