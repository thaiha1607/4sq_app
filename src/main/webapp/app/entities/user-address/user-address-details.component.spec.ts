/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import UserAddressDetails from './user-address-details.vue';
import UserAddressService from './user-address.service';
import AlertService from '@/shared/alert/alert.service';

type UserAddressDetailsComponentType = InstanceType<typeof UserAddressDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userAddressSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('UserAddress Management Detail Component', () => {
    let userAddressServiceStub: SinonStubbedInstance<UserAddressService>;
    let mountOptions: MountingOptions<UserAddressDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      userAddressServiceStub = sinon.createStubInstance<UserAddressService>(UserAddressService);

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
          userAddressService: () => userAddressServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userAddressServiceStub.find.resolves(userAddressSample);
        route = {
          params: {
            userAddressId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(UserAddressDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.userAddress).toMatchObject(userAddressSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userAddressServiceStub.find.resolves(userAddressSample);
        const wrapper = shallowMount(UserAddressDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
