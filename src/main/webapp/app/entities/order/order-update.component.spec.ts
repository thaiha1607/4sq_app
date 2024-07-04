/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import OrderUpdate from './order-update.vue';
import OrderService from './order.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import OrderStatusService from '@/entities/order-status/order-status.service';
import AddressService from '@/entities/address/address.service';

type OrderUpdateComponentType = InstanceType<typeof OrderUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<OrderUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Order Management Update Component', () => {
    let comp: OrderUpdateComponentType;
    let orderServiceStub: SinonStubbedInstance<OrderService>;

    beforeEach(() => {
      route = {};
      orderServiceStub = sinon.createStubInstance<OrderService>(OrderService);
      orderServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          orderService: () => orderServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          orderStatusService: () =>
            sinon.createStubInstance<OrderStatusService>(OrderStatusService, {
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
        const wrapper = shallowMount(OrderUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(OrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.order = orderSample;
        orderServiceStub.update.resolves(orderSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderServiceStub.update.calledWith(orderSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        orderServiceStub.create.resolves(entity);
        const wrapper = shallowMount(OrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.order = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        orderServiceStub.find.resolves(orderSample);
        orderServiceStub.retrieve.resolves([orderSample]);

        // WHEN
        route = {
          params: {
            orderId: '' + orderSample.id,
          },
        };
        const wrapper = shallowMount(OrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.order).toMatchObject(orderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderServiceStub.find.resolves(orderSample);
        const wrapper = shallowMount(OrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
