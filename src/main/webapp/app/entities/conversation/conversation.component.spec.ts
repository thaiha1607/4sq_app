/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Conversation from './conversation.vue';
import ConversationService from './conversation.service';
import AlertService from '@/shared/alert/alert.service';

type ConversationComponentType = InstanceType<typeof Conversation>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Conversation Management Component', () => {
    let conversationServiceStub: SinonStubbedInstance<ConversationService>;
    let mountOptions: MountingOptions<ConversationComponentType>['global'];

    beforeEach(() => {
      conversationServiceStub = sinon.createStubInstance<ConversationService>(ConversationService);
      conversationServiceStub.retrieve.resolves({ headers: {} });

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
          conversationService: () => conversationServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        conversationServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Conversation, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(conversationServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.conversations[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ConversationComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Conversation, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        conversationServiceStub.retrieve.reset();
        conversationServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        conversationServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeConversation();
        await comp.$nextTick(); // clear components

        // THEN
        expect(conversationServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(conversationServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
