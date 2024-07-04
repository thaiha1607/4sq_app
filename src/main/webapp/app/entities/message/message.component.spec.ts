/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Message from './message.vue';
import MessageService from './message.service';
import AlertService from '@/shared/alert/alert.service';

type MessageComponentType = InstanceType<typeof Message>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Message Management Component', () => {
    let messageServiceStub: SinonStubbedInstance<MessageService>;
    let mountOptions: MountingOptions<MessageComponentType>['global'];

    beforeEach(() => {
      messageServiceStub = sinon.createStubInstance<MessageService>(MessageService);
      messageServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          messageService: () => messageServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        messageServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Message, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(messageServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.messages[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: MessageComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Message, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        messageServiceStub.retrieve.reset();
        messageServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        messageServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeMessage();
        await comp.$nextTick(); // clear components

        // THEN
        expect(messageServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(messageServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
