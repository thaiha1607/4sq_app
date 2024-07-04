/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ShipmentStatusDetails from './shipment-status-details.vue';
import ShipmentStatusService from './shipment-status.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentStatusDetailsComponentType = InstanceType<typeof ShipmentStatusDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentStatusSample = { statusCode: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ShipmentStatus Management Detail Component', () => {
    let shipmentStatusServiceStub: SinonStubbedInstance<ShipmentStatusService>;
    let mountOptions: MountingOptions<ShipmentStatusDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shipmentStatusServiceStub = sinon.createStubInstance<ShipmentStatusService>(ShipmentStatusService);

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
          shipmentStatusService: () => shipmentStatusServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentStatusServiceStub.find.resolves(shipmentStatusSample);
        route = {
          params: {
            shipmentStatusId: '' + 123,
          },
        };
        const wrapper = shallowMount(ShipmentStatusDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shipmentStatus).toMatchObject(shipmentStatusSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentStatusServiceStub.find.resolves(shipmentStatusSample);
        const wrapper = shallowMount(ShipmentStatusDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
