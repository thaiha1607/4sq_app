/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ProductQuantityDetails from './product-quantity-details.vue';
import ProductQuantityService from './product-quantity.service';
import AlertService from '@/shared/alert/alert.service';

type ProductQuantityDetailsComponentType = InstanceType<typeof ProductQuantityDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const productQuantitySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ProductQuantity Management Detail Component', () => {
    let productQuantityServiceStub: SinonStubbedInstance<ProductQuantityService>;
    let mountOptions: MountingOptions<ProductQuantityDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      productQuantityServiceStub = sinon.createStubInstance<ProductQuantityService>(ProductQuantityService);

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
          productQuantityService: () => productQuantityServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        productQuantityServiceStub.find.resolves(productQuantitySample);
        route = {
          params: {
            productQuantityId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ProductQuantityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.productQuantity).toMatchObject(productQuantitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        productQuantityServiceStub.find.resolves(productQuantitySample);
        const wrapper = shallowMount(ProductQuantityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
