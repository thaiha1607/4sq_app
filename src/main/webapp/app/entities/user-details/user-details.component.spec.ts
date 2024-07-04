/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import UserDetails from './user-details.vue';
import UserDetailsService from './user-details.service';
import AlertService from '@/shared/alert/alert.service';

type UserDetailsComponentType = InstanceType<typeof UserDetails>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('UserDetails Management Component', () => {
    let userDetailsServiceStub: SinonStubbedInstance<UserDetailsService>;
    let mountOptions: MountingOptions<UserDetailsComponentType>['global'];

    beforeEach(() => {
      userDetailsServiceStub = sinon.createStubInstance<UserDetailsService>(UserDetailsService);
      userDetailsServiceStub.retrieve.resolves({ headers: {} });

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
          userDetailsService: () => userDetailsServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userDetailsServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(UserDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(userDetailsServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.userDetails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: UserDetailsComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(UserDetails, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        userDetailsServiceStub.retrieve.reset();
        userDetailsServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        userDetailsServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeUserDetails();
        await comp.$nextTick(); // clear components

        // THEN
        expect(userDetailsServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(userDetailsServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
