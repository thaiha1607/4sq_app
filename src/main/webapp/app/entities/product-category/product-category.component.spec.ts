/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ProductCategory from './product-category.vue';
import ProductCategoryService from './product-category.service';
import AlertService from '@/shared/alert/alert.service';

type ProductCategoryComponentType = InstanceType<typeof ProductCategory>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ProductCategory Management Component', () => {
    let productCategoryServiceStub: SinonStubbedInstance<ProductCategoryService>;
    let mountOptions: MountingOptions<ProductCategoryComponentType>['global'];

    beforeEach(() => {
      productCategoryServiceStub = sinon.createStubInstance<ProductCategoryService>(ProductCategoryService);
      productCategoryServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          productCategoryService: () => productCategoryServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        productCategoryServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(ProductCategory, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(productCategoryServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.productCategories[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ProductCategoryComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ProductCategory, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        productCategoryServiceStub.retrieve.reset();
        productCategoryServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        productCategoryServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeProductCategory();
        await comp.$nextTick(); // clear components

        // THEN
        expect(productCategoryServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(productCategoryServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
