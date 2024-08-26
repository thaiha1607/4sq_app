/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InternalOrderDetails from './internal-order-details.vue';
import InternalOrderService from './internal-order.service';
import AlertService from '@/shared/alert/alert.service';

type InternalOrderDetailsComponentType = InstanceType<typeof InternalOrderDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const internalOrderSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('InternalOrder Management Detail Component', () => {
    let internalOrderServiceStub: SinonStubbedInstance<InternalOrderService>;
    let mountOptions: MountingOptions<InternalOrderDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      internalOrderServiceStub = sinon.createStubInstance<InternalOrderService>(InternalOrderService);

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
          internalOrderService: () => internalOrderServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        internalOrderServiceStub.find.resolves(internalOrderSample);
        route = {
          params: {
            internalOrderId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(InternalOrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.internalOrder).toMatchObject(internalOrderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        internalOrderServiceStub.find.resolves(internalOrderSample);
        const wrapper = shallowMount(InternalOrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
