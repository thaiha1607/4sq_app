/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import InternalOrderUpdate from './internal-order-update.vue';
import InternalOrderService from './internal-order.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import OrderStatusService from '@/entities/order-status/order-status.service';
import OrderService from '@/entities/order/order.service';

type InternalOrderUpdateComponentType = InstanceType<typeof InternalOrderUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const internalOrderSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InternalOrderUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('InternalOrder Management Update Component', () => {
    let comp: InternalOrderUpdateComponentType;
    let internalOrderServiceStub: SinonStubbedInstance<InternalOrderService>;

    beforeEach(() => {
      route = {};
      internalOrderServiceStub = sinon.createStubInstance<InternalOrderService>(InternalOrderService);
      internalOrderServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          internalOrderService: () => internalOrderServiceStub,
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
        const wrapper = shallowMount(InternalOrderUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(InternalOrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.internalOrder = internalOrderSample;
        internalOrderServiceStub.update.resolves(internalOrderSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(internalOrderServiceStub.update.calledWith(internalOrderSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        internalOrderServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InternalOrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.internalOrder = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(internalOrderServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        internalOrderServiceStub.find.resolves(internalOrderSample);
        internalOrderServiceStub.retrieve.resolves([internalOrderSample]);

        // WHEN
        route = {
          params: {
            internalOrderId: '' + internalOrderSample.id,
          },
        };
        const wrapper = shallowMount(InternalOrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.internalOrder).toMatchObject(internalOrderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        internalOrderServiceStub.find.resolves(internalOrderSample);
        const wrapper = shallowMount(InternalOrderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
