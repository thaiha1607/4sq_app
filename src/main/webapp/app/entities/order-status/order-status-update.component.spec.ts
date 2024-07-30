/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import OrderStatusUpdate from './order-status-update.vue';
import OrderStatusService from './order-status.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type OrderStatusUpdateComponentType = InstanceType<typeof OrderStatusUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderStatusSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<OrderStatusUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('OrderStatus Management Update Component', () => {
    let comp: OrderStatusUpdateComponentType;
    let orderStatusServiceStub: SinonStubbedInstance<OrderStatusService>;

    beforeEach(() => {
      route = {};
      orderStatusServiceStub = sinon.createStubInstance<OrderStatusService>(OrderStatusService);
      orderStatusServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          orderStatusService: () => orderStatusServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(OrderStatusUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(OrderStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.orderStatus = orderStatusSample;
        orderStatusServiceStub.update.resolves(orderStatusSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderStatusServiceStub.update.calledWith(orderStatusSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        orderStatusServiceStub.create.resolves(entity);
        const wrapper = shallowMount(OrderStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.orderStatus = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderStatusServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        orderStatusServiceStub.find.resolves(orderStatusSample);
        orderStatusServiceStub.retrieve.resolves([orderStatusSample]);

        // WHEN
        route = {
          params: {
            orderStatusId: '' + orderStatusSample.id,
          },
        };
        const wrapper = shallowMount(OrderStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.orderStatus).toMatchObject(orderStatusSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderStatusServiceStub.find.resolves(orderStatusSample);
        const wrapper = shallowMount(OrderStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
