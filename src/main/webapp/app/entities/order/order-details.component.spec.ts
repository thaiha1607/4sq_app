/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrderDetails from './order-details.vue';
import OrderService from './order.service';
import AlertService from '@/shared/alert/alert.service';

type OrderDetailsComponentType = InstanceType<typeof OrderDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Order Management Detail Component', () => {
    let orderServiceStub: SinonStubbedInstance<OrderService>;
    let mountOptions: MountingOptions<OrderDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      orderServiceStub = sinon.createStubInstance<OrderService>(OrderService);

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
          orderService: () => orderServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderServiceStub.find.resolves(orderSample);
        route = {
          params: {
            orderId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(OrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.order).toMatchObject(orderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderServiceStub.find.resolves(orderSample);
        const wrapper = shallowMount(OrderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
