/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ProductQuantityUpdate from './product-quantity-update.vue';
import ProductQuantityService from './product-quantity.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import WorkingUnitService from '@/entities/working-unit/working-unit.service';
import ProductCategoryService from '@/entities/product-category/product-category.service';

type ProductQuantityUpdateComponentType = InstanceType<typeof ProductQuantityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const productQuantitySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ProductQuantityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ProductQuantity Management Update Component', () => {
    let comp: ProductQuantityUpdateComponentType;
    let productQuantityServiceStub: SinonStubbedInstance<ProductQuantityService>;

    beforeEach(() => {
      route = {};
      productQuantityServiceStub = sinon.createStubInstance<ProductQuantityService>(ProductQuantityService);
      productQuantityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          productQuantityService: () => productQuantityServiceStub,
          workingUnitService: () =>
            sinon.createStubInstance<WorkingUnitService>(WorkingUnitService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          productCategoryService: () =>
            sinon.createStubInstance<ProductCategoryService>(ProductCategoryService, {
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
        const wrapper = shallowMount(ProductQuantityUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ProductQuantityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.productQuantity = productQuantitySample;
        productQuantityServiceStub.update.resolves(productQuantitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productQuantityServiceStub.update.calledWith(productQuantitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        productQuantityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ProductQuantityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.productQuantity = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productQuantityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        productQuantityServiceStub.find.resolves(productQuantitySample);
        productQuantityServiceStub.retrieve.resolves([productQuantitySample]);

        // WHEN
        route = {
          params: {
            productQuantityId: '' + productQuantitySample.id,
          },
        };
        const wrapper = shallowMount(ProductQuantityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.productQuantity).toMatchObject(productQuantitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        productQuantityServiceStub.find.resolves(productQuantitySample);
        const wrapper = shallowMount(ProductQuantityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
