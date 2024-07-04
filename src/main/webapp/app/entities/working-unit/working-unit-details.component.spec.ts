/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import WorkingUnitDetails from './working-unit-details.vue';
import WorkingUnitService from './working-unit.service';
import AlertService from '@/shared/alert/alert.service';

type WorkingUnitDetailsComponentType = InstanceType<typeof WorkingUnitDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const workingUnitSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('WorkingUnit Management Detail Component', () => {
    let workingUnitServiceStub: SinonStubbedInstance<WorkingUnitService>;
    let mountOptions: MountingOptions<WorkingUnitDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      workingUnitServiceStub = sinon.createStubInstance<WorkingUnitService>(WorkingUnitService);

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
          workingUnitService: () => workingUnitServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        workingUnitServiceStub.find.resolves(workingUnitSample);
        route = {
          params: {
            workingUnitId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(WorkingUnitDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.workingUnit).toMatchObject(workingUnitSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        workingUnitServiceStub.find.resolves(workingUnitSample);
        const wrapper = shallowMount(WorkingUnitDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
