/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ShipmentItemDetails from './shipment-item-details.vue';
import ShipmentItemService from './shipment-item.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentItemDetailsComponentType = InstanceType<typeof ShipmentItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentItemSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ShipmentItem Management Detail Component', () => {
    let shipmentItemServiceStub: SinonStubbedInstance<ShipmentItemService>;
    let mountOptions: MountingOptions<ShipmentItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shipmentItemServiceStub = sinon.createStubInstance<ShipmentItemService>(ShipmentItemService);

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
          shipmentItemService: () => shipmentItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentItemServiceStub.find.resolves(shipmentItemSample);
        route = {
          params: {
            shipmentItemId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ShipmentItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shipmentItem).toMatchObject(shipmentItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentItemServiceStub.find.resolves(shipmentItemSample);
        const wrapper = shallowMount(ShipmentItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
