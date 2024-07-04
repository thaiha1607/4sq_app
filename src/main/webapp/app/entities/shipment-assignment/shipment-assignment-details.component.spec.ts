/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ShipmentAssignmentDetails from './shipment-assignment-details.vue';
import ShipmentAssignmentService from './shipment-assignment.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentAssignmentDetailsComponentType = InstanceType<typeof ShipmentAssignmentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentAssignmentSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ShipmentAssignment Management Detail Component', () => {
    let shipmentAssignmentServiceStub: SinonStubbedInstance<ShipmentAssignmentService>;
    let mountOptions: MountingOptions<ShipmentAssignmentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shipmentAssignmentServiceStub = sinon.createStubInstance<ShipmentAssignmentService>(ShipmentAssignmentService);

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
          shipmentAssignmentService: () => shipmentAssignmentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentAssignmentServiceStub.find.resolves(shipmentAssignmentSample);
        route = {
          params: {
            shipmentAssignmentId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ShipmentAssignmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shipmentAssignment).toMatchObject(shipmentAssignmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentAssignmentServiceStub.find.resolves(shipmentAssignmentSample);
        const wrapper = shallowMount(ShipmentAssignmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
