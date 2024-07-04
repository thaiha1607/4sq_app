/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrderStatusDetails from './order-status-details.vue';
import OrderStatusService from './order-status.service';
import AlertService from '@/shared/alert/alert.service';

type OrderStatusDetailsComponentType = InstanceType<typeof OrderStatusDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderStatusSample = { statusCode: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('OrderStatus Management Detail Component', () => {
    let orderStatusServiceStub: SinonStubbedInstance<OrderStatusService>;
    let mountOptions: MountingOptions<OrderStatusDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      orderStatusServiceStub = sinon.createStubInstance<OrderStatusService>(OrderStatusService);

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
          orderStatusService: () => orderStatusServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderStatusServiceStub.find.resolves(orderStatusSample);
        route = {
          params: {
            orderStatusId: '' + 123,
          },
        };
        const wrapper = shallowMount(OrderStatusDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.orderStatus).toMatchObject(orderStatusSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderStatusServiceStub.find.resolves(orderStatusSample);
        const wrapper = shallowMount(OrderStatusDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
