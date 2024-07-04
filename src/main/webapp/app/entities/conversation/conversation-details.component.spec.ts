/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ConversationDetails from './conversation-details.vue';
import ConversationService from './conversation.service';
import AlertService from '@/shared/alert/alert.service';

type ConversationDetailsComponentType = InstanceType<typeof ConversationDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const conversationSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Conversation Management Detail Component', () => {
    let conversationServiceStub: SinonStubbedInstance<ConversationService>;
    let mountOptions: MountingOptions<ConversationDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      conversationServiceStub = sinon.createStubInstance<ConversationService>(ConversationService);

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
          conversationService: () => conversationServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        conversationServiceStub.find.resolves(conversationSample);
        route = {
          params: {
            conversationId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(ConversationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.conversation).toMatchObject(conversationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        conversationServiceStub.find.resolves(conversationSample);
        const wrapper = shallowMount(ConversationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
