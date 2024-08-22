/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrderHistoryDetails from './order-history-details.vue';
import OrderHistoryService from './order-history.service';
import AlertService from '@/shared/alert/alert.service';

type OrderHistoryDetailsComponentType = InstanceType<typeof OrderHistoryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderHistorySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('OrderHistory Management Detail Component', () => {
    let orderHistoryServiceStub: SinonStubbedInstance<OrderHistoryService>;
    let mountOptions: MountingOptions<OrderHistoryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      orderHistoryServiceStub = sinon.createStubInstance<OrderHistoryService>(OrderHistoryService);

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
          orderHistoryService: () => orderHistoryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderHistoryServiceStub.find.resolves(orderHistorySample);
        route = {
          params: {
            orderHistoryId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(OrderHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.orderHistory).toMatchObject(orderHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderHistoryServiceStub.find.resolves(orderHistorySample);
        const wrapper = shallowMount(OrderHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
