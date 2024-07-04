/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ProductImageUpdate from './product-image-update.vue';
import ProductImageService from './product-image.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import ProductService from '@/entities/product/product.service';

type ProductImageUpdateComponentType = InstanceType<typeof ProductImageUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const productImageSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ProductImageUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ProductImage Management Update Component', () => {
    let comp: ProductImageUpdateComponentType;
    let productImageServiceStub: SinonStubbedInstance<ProductImageService>;

    beforeEach(() => {
      route = {};
      productImageServiceStub = sinon.createStubInstance<ProductImageService>(ProductImageService);
      productImageServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          productImageService: () => productImageServiceStub,
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
        const wrapper = shallowMount(ProductImageUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ProductImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.productImage = productImageSample;
        productImageServiceStub.update.resolves(productImageSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productImageServiceStub.update.calledWith(productImageSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        productImageServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ProductImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.productImage = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productImageServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        productImageServiceStub.find.resolves(productImageSample);
        productImageServiceStub.retrieve.resolves([productImageSample]);

        // WHEN
        route = {
          params: {
            productImageId: '' + productImageSample.id,
          },
        };
        const wrapper = shallowMount(ProductImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.productImage).toMatchObject(productImageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        productImageServiceStub.find.resolves(productImageSample);
        const wrapper = shallowMount(ProductImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
