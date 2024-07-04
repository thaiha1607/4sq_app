import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ProductQuantityService from './product-quantity.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import WorkingUnitService from '@/entities/working-unit/working-unit.service';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import ProductCategoryService from '@/entities/product-category/product-category.service';
import { type IProductCategory } from '@/shared/model/product-category.model';
import { type IProductQuantity, ProductQuantity } from '@/shared/model/product-quantity.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProductQuantityUpdate',
  setup() {
    const productQuantityService = inject('productQuantityService', () => new ProductQuantityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const productQuantity: Ref<IProductQuantity> = ref(new ProductQuantity());

    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());

    const workingUnits: Ref<IWorkingUnit[]> = ref([]);

    const productCategoryService = inject('productCategoryService', () => new ProductCategoryService());

    const productCategories: Ref<IProductCategory[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProductQuantity = async productQuantityId => {
      try {
        const res = await productQuantityService().find(productQuantityId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        productQuantity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productQuantityId) {
      retrieveProductQuantity(route.params.productQuantityId);
    }

    const initRelationships = () => {
      workingUnitService()
        .retrieve()
        .then(res => {
          workingUnits.value = res.data;
        });
      productCategoryService()
        .retrieve()
        .then(res => {
          productCategories.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      qty: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 0.', 0),
      },
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      workingUnit: {
        required: validations.required('This field is required.'),
      },
      productCategory: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, productQuantity as any);
    v$.value.$validate();

    return {
      productQuantityService,
      alertService,
      productQuantity,
      previousState,
      isSaving,
      currentLanguage,
      workingUnits,
      productCategories,
      v$,
      ...useDateFormat({ entityRef: productQuantity }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.productQuantity.id) {
        this.productQuantityService()
          .update(this.productQuantity)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A ProductQuantity is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.productQuantityService()
          .create(this.productQuantity)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A ProductQuantity is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
