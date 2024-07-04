/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ProductQuantity from './product-quantity.vue';
import ProductQuantityService from './product-quantity.service';
import AlertService from '@/shared/alert/alert.service';

type ProductQuantityComponentType = InstanceType<typeof ProductQuantity>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ProductQuantity Management Component', () => {
    let productQuantityServiceStub: SinonStubbedInstance<ProductQuantityService>;
    let mountOptions: MountingOptions<ProductQuantityComponentType>['global'];

    beforeEach(() => {
      productQuantityServiceStub = sinon.createStubInstance<ProductQuantityService>(ProductQuantityService);
      productQuantityServiceStub.retrieve.resolves({ headers: {} });

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
          productQuantityService: () => productQuantityServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        productQuantityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(ProductQuantity, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(productQuantityServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.productQuantities[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ProductQuantityComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ProductQuantity, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        productQuantityServiceStub.retrieve.reset();
        productQuantityServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        productQuantityServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeProductQuantity();
        await comp.$nextTick(); // clear components

        // THEN
        expect(productQuantityServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(productQuantityServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
