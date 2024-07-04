/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ShipmentDetails from './shipment-details.vue';
import ShipmentService from './shipment.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentDetailsComponentType = InstanceType<typeof ShipmentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Shipment Management Detail Component', () => {
    let shipmentServiceStub: SinonStubbedInstance<ShipmentService>;
    let mountOptions: MountingOptions<ShipmentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shipmentServiceStub = sinon.createStubInstance<ShipmentService>(ShipmentService);

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
          shipmentService: () => shipmentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentServiceStub.find.resolves(shipmentSample);
        route = {
          params: {
            shipmentId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ShipmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shipment).toMatchObject(shipmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentServiceStub.find.resolves(shipmentSample);
        const wrapper = shallowMount(ShipmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
