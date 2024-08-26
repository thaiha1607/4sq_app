/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import InternalOrderItemUpdate from './internal-order-item-update.vue';
import InternalOrderItemService from './internal-order-item.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import OrderItemService from '@/entities/order-item/order-item.service';

type InternalOrderItemUpdateComponentType = InstanceType<typeof InternalOrderItemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const internalOrderItemSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InternalOrderItemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('InternalOrderItem Management Update Component', () => {
    let comp: InternalOrderItemUpdateComponentType;
    let internalOrderItemServiceStub: SinonStubbedInstance<InternalOrderItemService>;

    beforeEach(() => {
      route = {};
      internalOrderItemServiceStub = sinon.createStubInstance<InternalOrderItemService>(InternalOrderItemService);
      internalOrderItemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          internalOrderItemService: () => internalOrderItemServiceStub,
          orderItemService: () =>
            sinon.createStubInstance<OrderItemService>(OrderItemService, {
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
        const wrapper = shallowMount(InternalOrderItemUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(InternalOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.internalOrderItem = internalOrderItemSample;
        internalOrderItemServiceStub.update.resolves(internalOrderItemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(internalOrderItemServiceStub.update.calledWith(internalOrderItemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        internalOrderItemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InternalOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.internalOrderItem = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(internalOrderItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        internalOrderItemServiceStub.find.resolves(internalOrderItemSample);
        internalOrderItemServiceStub.retrieve.resolves([internalOrderItemSample]);

        // WHEN
        route = {
          params: {
            internalOrderItemId: '' + internalOrderItemSample.id,
          },
        };
        const wrapper = shallowMount(InternalOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.internalOrderItem).toMatchObject(internalOrderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        internalOrderItemServiceStub.find.resolves(internalOrderItemSample);
        const wrapper = shallowMount(InternalOrderItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
