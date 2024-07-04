/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Participant from './participant.vue';
import ParticipantService from './participant.service';
import AlertService from '@/shared/alert/alert.service';

type ParticipantComponentType = InstanceType<typeof Participant>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Participant Management Component', () => {
    let participantServiceStub: SinonStubbedInstance<ParticipantService>;
    let mountOptions: MountingOptions<ParticipantComponentType>['global'];

    beforeEach(() => {
      participantServiceStub = sinon.createStubInstance<ParticipantService>(ParticipantService);
      participantServiceStub.retrieve.resolves({ headers: {} });

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
          participantService: () => participantServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        participantServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Participant, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(participantServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.participants[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ParticipantComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Participant, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        participantServiceStub.retrieve.reset();
        participantServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        participantServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeParticipant();
        await comp.$nextTick(); // clear components

        // THEN
        expect(participantServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(participantServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
