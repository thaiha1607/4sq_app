/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ConversationUpdate from './conversation-update.vue';
import ConversationService from './conversation.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type ConversationUpdateComponentType = InstanceType<typeof ConversationUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const conversationSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ConversationUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Conversation Management Update Component', () => {
    let comp: ConversationUpdateComponentType;
    let conversationServiceStub: SinonStubbedInstance<ConversationService>;

    beforeEach(() => {
      route = {};
      conversationServiceStub = sinon.createStubInstance<ConversationService>(ConversationService);
      conversationServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          conversationService: () => conversationServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ConversationUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ConversationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.conversation = conversationSample;
        conversationServiceStub.update.resolves(conversationSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(conversationServiceStub.update.calledWith(conversationSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        conversationServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ConversationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.conversation = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(conversationServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        conversationServiceStub.find.resolves(conversationSample);
        conversationServiceStub.retrieve.resolves([conversationSample]);

        // WHEN
        route = {
          params: {
            conversationId: '' + conversationSample.id,
          },
        };
        const wrapper = shallowMount(ConversationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.conversation).toMatchObject(conversationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        conversationServiceStub.find.resolves(conversationSample);
        const wrapper = shallowMount(ConversationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
