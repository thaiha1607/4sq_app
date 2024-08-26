/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import InternalOrderHistoryUpdate from './internal-order-history-update.vue';
import InternalOrderHistoryService from './internal-order-history.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import OrderStatusService from '@/entities/order-status/order-status.service';
import InternalOrderService from '@/entities/internal-order/internal-order.service';

type InternalOrderHistoryUpdateComponentType = InstanceType<typeof InternalOrderHistoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const internalOrderHistorySample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InternalOrderHistoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('InternalOrderHistory Management Update Component', () => {
    let comp: InternalOrderHistoryUpdateComponentType;
    let internalOrderHistoryServiceStub: SinonStubbedInstance<InternalOrderHistoryService>;

    beforeEach(() => {
      route = {};
      internalOrderHistoryServiceStub = sinon.createStubInstance<InternalOrderHistoryService>(InternalOrderHistoryService);
      internalOrderHistoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          internalOrderHistoryService: () => internalOrderHistoryServiceStub,
          orderStatusService: () =>
            sinon.createStubInstance<OrderStatusService>(OrderStatusService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          internalOrderService: () =>
            sinon.createStubInstance<InternalOrderService>(InternalOrderService, {
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
        const wrapper = shallowMount(InternalOrderHistoryUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(InternalOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.internalOrderHistory = internalOrderHistorySample;
        internalOrderHistoryServiceStub.update.resolves(internalOrderHistorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(internalOrderHistoryServiceStub.update.calledWith(internalOrderHistorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        internalOrderHistoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InternalOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.internalOrderHistory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(internalOrderHistoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        internalOrderHistoryServiceStub.find.resolves(internalOrderHistorySample);
        internalOrderHistoryServiceStub.retrieve.resolves([internalOrderHistorySample]);

        // WHEN
        route = {
          params: {
            internalOrderHistoryId: '' + internalOrderHistorySample.id,
          },
        };
        const wrapper = shallowMount(InternalOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.internalOrderHistory).toMatchObject(internalOrderHistorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        internalOrderHistoryServiceStub.find.resolves(internalOrderHistorySample);
        const wrapper = shallowMount(InternalOrderHistoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
