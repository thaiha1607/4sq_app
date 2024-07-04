/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ShipmentItemUpdate from './shipment-item-update.vue';
import ShipmentItemService from './shipment-item.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import OrderItemService from '@/entities/order-item/order-item.service';
import ShipmentService from '@/entities/shipment/shipment.service';

type ShipmentItemUpdateComponentType = InstanceType<typeof ShipmentItemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentItemSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShipmentItemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ShipmentItem Management Update Component', () => {
    let comp: ShipmentItemUpdateComponentType;
    let shipmentItemServiceStub: SinonStubbedInstance<ShipmentItemService>;

    beforeEach(() => {
      route = {};
      shipmentItemServiceStub = sinon.createStubInstance<ShipmentItemService>(ShipmentItemService);
      shipmentItemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          shipmentItemService: () => shipmentItemServiceStub,
          orderItemService: () =>
            sinon.createStubInstance<OrderItemService>(OrderItemService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          shipmentService: () =>
            sinon.createStubInstance<ShipmentService>(ShipmentService, {
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
        const wrapper = shallowMount(ShipmentItemUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ShipmentItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipmentItem = shipmentItemSample;
        shipmentItemServiceStub.update.resolves(shipmentItemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentItemServiceStub.update.calledWith(shipmentItemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shipmentItemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShipmentItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipmentItem = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        shipmentItemServiceStub.find.resolves(shipmentItemSample);
        shipmentItemServiceStub.retrieve.resolves([shipmentItemSample]);

        // WHEN
        route = {
          params: {
            shipmentItemId: '' + shipmentItemSample.id,
          },
        };
        const wrapper = shallowMount(ShipmentItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shipmentItem).toMatchObject(shipmentItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentItemServiceStub.find.resolves(shipmentItemSample);
        const wrapper = shallowMount(ShipmentItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
