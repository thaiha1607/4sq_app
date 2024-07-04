/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import UserDetailsUpdate from './user-details-update.vue';
import UserDetailsService from './user-details.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import WorkingUnitService from '@/entities/working-unit/working-unit.service';

type UserDetailsUpdateComponentType = InstanceType<typeof UserDetailsUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userDetailsSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<UserDetailsUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('UserDetails Management Update Component', () => {
    let comp: UserDetailsUpdateComponentType;
    let userDetailsServiceStub: SinonStubbedInstance<UserDetailsService>;

    beforeEach(() => {
      route = {};
      userDetailsServiceStub = sinon.createStubInstance<UserDetailsService>(UserDetailsService);
      userDetailsServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          userDetailsService: () => userDetailsServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          workingUnitService: () =>
            sinon.createStubInstance<WorkingUnitService>(WorkingUnitService, {
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
        const wrapper = shallowMount(UserDetailsUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(UserDetailsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userDetails = userDetailsSample;
        userDetailsServiceStub.update.resolves(userDetailsSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userDetailsServiceStub.update.calledWith(userDetailsSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        userDetailsServiceStub.create.resolves(entity);
        const wrapper = shallowMount(UserDetailsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userDetails = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userDetailsServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        userDetailsServiceStub.find.resolves(userDetailsSample);
        userDetailsServiceStub.retrieve.resolves([userDetailsSample]);

        // WHEN
        route = {
          params: {
            userDetailsId: '' + userDetailsSample.id,
          },
        };
        const wrapper = shallowMount(UserDetailsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.userDetails).toMatchObject(userDetailsSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userDetailsServiceStub.find.resolves(userDetailsSample);
        const wrapper = shallowMount(UserDetailsUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
