/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ProductCategoryUpdate from './product-category-update.vue';
import ProductCategoryService from './product-category.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import ColourService from '@/entities/colour/colour.service';
import ProductService from '@/entities/product/product.service';

type ProductCategoryUpdateComponentType = InstanceType<typeof ProductCategoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const productCategorySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ProductCategoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ProductCategory Management Update Component', () => {
    let comp: ProductCategoryUpdateComponentType;
    let productCategoryServiceStub: SinonStubbedInstance<ProductCategoryService>;

    beforeEach(() => {
      route = {};
      productCategoryServiceStub = sinon.createStubInstance<ProductCategoryService>(ProductCategoryService);
      productCategoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          productCategoryService: () => productCategoryServiceStub,
          colourService: () =>
            sinon.createStubInstance<ColourService>(ColourService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          productService: () =>
            sinon.createStubInstance<ProductService>(ProductService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ProductCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ProductCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.productCategory = productCategorySample;
        productCategoryServiceStub.update.resolves(productCategorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productCategoryServiceStub.update.calledWith(productCategorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        productCategoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ProductCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.productCategory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productCategoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        productCategoryServiceStub.find.resolves(productCategorySample);
        productCategoryServiceStub.retrieve.resolves([productCategorySample]);

        // WHEN
        route = {
          params: {
            productCategoryId: '' + productCategorySample.id,
          },
        };
        const wrapper = shallowMount(ProductCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.productCategory).toMatchObject(productCategorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        productCategoryServiceStub.find.resolves(productCategorySample);
        const wrapper = shallowMount(ProductCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
