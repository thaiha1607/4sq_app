/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import StaffInfoDetails from './staff-info-details.vue';
import StaffInfoService from './staff-info.service';
import AlertService from '@/shared/alert/alert.service';

type StaffInfoDetailsComponentType = InstanceType<typeof StaffInfoDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const staffInfoSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('StaffInfo Management Detail Component', () => {
    let staffInfoServiceStub: SinonStubbedInstance<StaffInfoService>;
    let mountOptions: MountingOptions<StaffInfoDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      staffInfoServiceStub = sinon.createStubInstance<StaffInfoService>(StaffInfoService);

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
          staffInfoService: () => staffInfoServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        staffInfoServiceStub.find.resolves(staffInfoSample);
        route = {
          params: {
            staffInfoId: '' + 123,
          },
        };
        const wrapper = shallowMount(StaffInfoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.staffInfo).toMatchObject(staffInfoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        staffInfoServiceStub.find.resolves(staffInfoSample);
        const wrapper = shallowMount(StaffInfoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
