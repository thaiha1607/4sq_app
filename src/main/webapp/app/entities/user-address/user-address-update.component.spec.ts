/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import UserAddressUpdate from './user-address-update.vue';
import UserAddressService from './user-address.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import AddressService from '@/entities/address/address.service';

type UserAddressUpdateComponentType = InstanceType<typeof UserAddressUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userAddressSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<UserAddressUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('UserAddress Management Update Component', () => {
    let comp: UserAddressUpdateComponentType;
    let userAddressServiceStub: SinonStubbedInstance<UserAddressService>;

    beforeEach(() => {
      route = {};
      userAddressServiceStub = sinon.createStubInstance<UserAddressService>(UserAddressService);
      userAddressServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          userAddressService: () => userAddressServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          addressService: () =>
            sinon.createStubInstance<AddressService>(AddressService, {
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
        const wrapper = shallowMount(UserAddressUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(UserAddressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userAddress = userAddressSample;
        userAddressServiceStub.update.resolves(userAddressSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userAddressServiceStub.update.calledWith(userAddressSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        userAddressServiceStub.create.resolves(entity);
        const wrapper = shallowMount(UserAddressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userAddress = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userAddressServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        userAddressServiceStub.find.resolves(userAddressSample);
        userAddressServiceStub.retrieve.resolves([userAddressSample]);

        // WHEN
        route = {
          params: {
            userAddressId: '' + userAddressSample.id,
          },
        };
        const wrapper = shallowMount(UserAddressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.userAddress).toMatchObject(userAddressSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userAddressServiceStub.find.resolves(userAddressSample);
        const wrapper = shallowMount(UserAddressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
