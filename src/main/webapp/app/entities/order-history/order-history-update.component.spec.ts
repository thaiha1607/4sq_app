/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import OrderHistoryUpdate from './order-history-update.vue';
import OrderHistoryService from './order-history.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import OrderStatusService from '@/entities/order-status/order-status.service';
import OrderService from '@/entities/order/order.service';

type OrderHistoryUpdateComponentType = InstanceType<typeof OrderHistoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderHistorySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<OrderHistoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('OrderHistory Management Update Component', () => {
    let comp: OrderHistoryUpdateComponentType;
    let orderHistoryServiceStub: SinonStubbedInstance<OrderHistoryService>;

    beforeEach(() => {
      route = {};
      orderHistoryServiceStub = sinon.createStubInstance<OrderHistoryService>(OrderHistoryService);
      orderHistoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          orderHistoryService: () => orderHistoryServiceStub,
          orderStatusService: () =>
            sinon.createStubInstance<OrderStatusService>(OrderStatusService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          orderService: () =>
            sinon.createStubInstance<OrderService>(OrderService, {
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
        const wrapper = shallowMount(OrderHistoryUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(OrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.orderHistory = orderHistorySample;
        orderHistoryServiceStub.update.resolves(orderHistorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderHistoryServiceStub.update.calledWith(orderHistorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        orderHistoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(OrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.orderHistory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderHistoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        orderHistoryServiceStub.find.resolves(orderHistorySample);
        orderHistoryServiceStub.retrieve.resolves([orderHistorySample]);

        // WHEN
        route = {
          params: {
            orderHistoryId: '' + orderHistorySample.id,
          },
        };
        const wrapper = shallowMount(OrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.orderHistory).toMatchObject(orderHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderHistoryServiceStub.find.resolves(orderHistorySample);
        const wrapper = shallowMount(OrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
