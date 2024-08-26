/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InternalOrderItemDetails from './internal-order-item-details.vue';
import InternalOrderItemService from './internal-order-item.service';
import AlertService from '@/shared/alert/alert.service';

type InternalOrderItemDetailsComponentType = InstanceType<typeof InternalOrderItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const internalOrderItemSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('InternalOrderItem Management Detail Component', () => {
    let internalOrderItemServiceStub: SinonStubbedInstance<InternalOrderItemService>;
    let mountOptions: MountingOptions<InternalOrderItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      internalOrderItemServiceStub = sinon.createStubInstance<InternalOrderItemService>(InternalOrderItemService);

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
          internalOrderItemService: () => internalOrderItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        internalOrderItemServiceStub.find.resolves(internalOrderItemSample);
        route = {
          params: {
            internalOrderItemId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(InternalOrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.internalOrderItem).toMatchObject(internalOrderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        internalOrderItemServiceStub.find.resolves(internalOrderItemSample);
        const wrapper = shallowMount(InternalOrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
