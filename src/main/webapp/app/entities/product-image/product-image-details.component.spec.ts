/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ProductImageDetails from './product-image-details.vue';
import ProductImageService from './product-image.service';
import AlertService from '@/shared/alert/alert.service';

type ProductImageDetailsComponentType = InstanceType<typeof ProductImageDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const productImageSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ProductImage Management Detail Component', () => {
    let productImageServiceStub: SinonStubbedInstance<ProductImageService>;
    let mountOptions: MountingOptions<ProductImageDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      productImageServiceStub = sinon.createStubInstance<ProductImageService>(ProductImageService);

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          productImageService: () => productImageServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        productImageServiceStub.find.resolves(productImageSample);
        route = {
          params: {
            productImageId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ProductImageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.productImage).toMatchObject(productImageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        productImageServiceStub.find.resolves(productImageSample);
        const wrapper = shallowMount(ProductImageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
