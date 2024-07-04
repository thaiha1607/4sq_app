/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MessageDetails from './message-details.vue';
import MessageService from './message.service';
import AlertService from '@/shared/alert/alert.service';

type MessageDetailsComponentType = InstanceType<typeof MessageDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const messageSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Message Management Detail Component', () => {
    let messageServiceStub: SinonStubbedInstance<MessageService>;
    let mountOptions: MountingOptions<MessageDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      messageServiceStub = sinon.createStubInstance<MessageService>(MessageService);

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
          messageService: () => messageServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        messageServiceStub.find.resolves(messageSample);
        route = {
          params: {
            messageId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(MessageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.message).toMatchObject(messageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        messageServiceStub.find.resolves(messageSample);
        const wrapper = shallowMount(MessageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
