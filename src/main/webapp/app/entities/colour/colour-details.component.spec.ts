/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ColourDetails from './colour-details.vue';
import ColourService from './colour.service';
import AlertService from '@/shared/alert/alert.service';

type ColourDetailsComponentType = InstanceType<typeof ColourDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const colourSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Colour Management Detail Component', () => {
    let colourServiceStub: SinonStubbedInstance<ColourService>;
    let mountOptions: MountingOptions<ColourDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      colourServiceStub = sinon.createStubInstance<ColourService>(ColourService);

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
          colourService: () => colourServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        colourServiceStub.find.resolves(colourSample);
        route = {
          params: {
            colourId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ColourDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.colour).toMatchObject(colourSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        colourServiceStub.find.resolves(colourSample);
        const wrapper = shallowMount(ColourDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
