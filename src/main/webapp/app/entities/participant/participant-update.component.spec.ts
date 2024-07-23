/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ParticipantUpdate from './participant-update.vue';
import ParticipantService from './participant.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import ConversationService from '@/entities/conversation/conversation.service';
import MessageService from '@/entities/message/message.service';

type ParticipantUpdateComponentType = InstanceType<typeof ParticipantUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const participantSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ParticipantUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Participant Management Update Component', () => {
    let comp: ParticipantUpdateComponentType;
    let participantServiceStub: SinonStubbedInstance<ParticipantService>;

    beforeEach(() => {
      route = {};
      participantServiceStub = sinon.createStubInstance<ParticipantService>(ParticipantService);
      participantServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          participantService: () => participantServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          conversationService: () =>
            sinon.createStubInstance<ConversationService>(ConversationService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          messageService: () =>
            sinon.createStubInstance<MessageService>(MessageService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ParticipantUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ParticipantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.participant = participantSample;
        participantServiceStub.update.resolves(participantSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(participantServiceStub.update.calledWith(participantSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        participantServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ParticipantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.participant = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(participantServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        participantServiceStub.find.resolves(participantSample);
        participantServiceStub.retrieve.resolves([participantSample]);

        // WHEN
        route = {
          params: {
            participantId: '' + participantSample.id,
          },
        };
        const wrapper = shallowMount(ParticipantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.participant).toMatchObject(participantSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        participantServiceStub.find.resolves(participantSample);
        const wrapper = shallowMount(ParticipantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
