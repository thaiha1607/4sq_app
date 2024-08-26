/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InternalOrderHistoryDetails from './internal-order-history-details.vue';
import InternalOrderHistoryService from './internal-order-history.service';
import AlertService from '@/shared/alert/alert.service';

type InternalOrderHistoryDetailsComponentType = InstanceType<typeof InternalOrderHistoryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const internalOrderHistorySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('InternalOrderHistory Management Detail Component', () => {
    let internalOrderHistoryServiceStub: SinonStubbedInstance<InternalOrderHistoryService>;
    let mountOptions: MountingOptions<InternalOrderHistoryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      internalOrderHistoryServiceStub = sinon.createStubInstance<InternalOrderHistoryService>(InternalOrderHistoryService);

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
          internalOrderHistoryService: () => internalOrderHistoryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        internalOrderHistoryServiceStub.find.resolves(internalOrderHistorySample);
        route = {
          params: {
            internalOrderHistoryId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(InternalOrderHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.internalOrderHistory).toMatchObject(internalOrderHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        internalOrderHistoryServiceStub.find.resolves(internalOrderHistorySample);
        const wrapper = shallowMount(InternalOrderHistoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
