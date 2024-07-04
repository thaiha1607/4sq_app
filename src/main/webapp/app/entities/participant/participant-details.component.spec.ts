/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ParticipantDetails from './participant-details.vue';
import ParticipantService from './participant.service';
import AlertService from '@/shared/alert/alert.service';

type ParticipantDetailsComponentType = InstanceType<typeof ParticipantDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const participantSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Participant Management Detail Component', () => {
    let participantServiceStub: SinonStubbedInstance<ParticipantService>;
    let mountOptions: MountingOptions<ParticipantDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      participantServiceStub = sinon.createStubInstance<ParticipantService>(ParticipantService);

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
          participantService: () => participantServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        participantServiceStub.find.resolves(participantSample);
        route = {
          params: {
            participantId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ParticipantDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.participant).toMatchObject(participantSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        participantServiceStub.find.resolves(participantSample);
        const wrapper = shallowMount(ParticipantDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
