/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import UserDetailsDetails from './user-details-details.vue';
import UserDetailsService from './user-details.service';
import AlertService from '@/shared/alert/alert.service';

type UserDetailsDetailsComponentType = InstanceType<typeof UserDetailsDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userDetailsSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('UserDetails Management Detail Component', () => {
    let userDetailsServiceStub: SinonStubbedInstance<UserDetailsService>;
    let mountOptions: MountingOptions<UserDetailsDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      userDetailsServiceStub = sinon.createStubInstance<UserDetailsService>(UserDetailsService);

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
          userDetailsService: () => userDetailsServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userDetailsServiceStub.find.resolves(userDetailsSample);
        route = {
          params: {
            userDetailsId: '' + 123,
          },
        };
        const wrapper = shallowMount(UserDetailsDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.userDetails).toMatchObject(userDetailsSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userDetailsServiceStub.find.resolves(userDetailsSample);
        const wrapper = shallowMount(UserDetailsDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
