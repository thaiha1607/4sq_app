/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrderItemDetails from './order-item-details.vue';
import OrderItemService from './order-item.service';
import AlertService from '@/shared/alert/alert.service';

type OrderItemDetailsComponentType = InstanceType<typeof OrderItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderItemSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('OrderItem Management Detail Component', () => {
    let orderItemServiceStub: SinonStubbedInstance<OrderItemService>;
    let mountOptions: MountingOptions<OrderItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      orderItemServiceStub = sinon.createStubInstance<OrderItemService>(OrderItemService);

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
          orderItemService: () => orderItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderItemServiceStub.find.resolves(orderItemSample);
        route = {
          params: {
            orderItemId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(OrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.orderItem).toMatchObject(orderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderItemServiceStub.find.resolves(orderItemSample);
        const wrapper = shallowMount(OrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
