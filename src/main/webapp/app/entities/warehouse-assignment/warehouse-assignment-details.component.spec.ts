/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import WarehouseAssignmentDetails from './warehouse-assignment-details.vue';
import WarehouseAssignmentService from './warehouse-assignment.service';
import AlertService from '@/shared/alert/alert.service';

type WarehouseAssignmentDetailsComponentType = InstanceType<typeof WarehouseAssignmentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const warehouseAssignmentSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('WarehouseAssignment Management Detail Component', () => {
    let warehouseAssignmentServiceStub: SinonStubbedInstance<WarehouseAssignmentService>;
    let mountOptions: MountingOptions<WarehouseAssignmentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      warehouseAssignmentServiceStub = sinon.createStubInstance<WarehouseAssignmentService>(WarehouseAssignmentService);

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
          warehouseAssignmentService: () => warehouseAssignmentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        warehouseAssignmentServiceStub.find.resolves(warehouseAssignmentSample);
        route = {
          params: {
            warehouseAssignmentId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(WarehouseAssignmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.warehouseAssignment).toMatchObject(warehouseAssignmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        warehouseAssignmentServiceStub.find.resolves(warehouseAssignmentSample);
        const wrapper = shallowMount(WarehouseAssignmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
