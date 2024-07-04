/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ProductImage from './product-image.vue';
import ProductImageService from './product-image.service';
import AlertService from '@/shared/alert/alert.service';

type ProductImageComponentType = InstanceType<typeof ProductImage>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ProductImage Management Component', () => {
    let productImageServiceStub: SinonStubbedInstance<ProductImageService>;
    let mountOptions: MountingOptions<ProductImageComponentType>['global'];

    beforeEach(() => {
      productImageServiceStub = sinon.createStubInstance<ProductImageService>(ProductImageService);
      productImageServiceStub.retrieve.resolves({ headers: {} });

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
          productImageService: () => productImageServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        productImageServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(ProductImage, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(productImageServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.productImages[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ProductImageComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ProductImage, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        productImageServiceStub.retrieve.reset();
        productImageServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        productImageServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeProductImage();
        await comp.$nextTick(); // clear components

        // THEN
        expect(productImageServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(productImageServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
