/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ShipmentUpdate from './shipment-update.vue';
import ShipmentService from './shipment.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import ShipmentStatusService from '@/entities/shipment-status/shipment-status.service';
import OrderService from '@/entities/order/order.service';
import InvoiceService from '@/entities/invoice/invoice.service';

type ShipmentUpdateComponentType = InstanceType<typeof ShipmentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShipmentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Shipment Management Update Component', () => {
    let comp: ShipmentUpdateComponentType;
    let shipmentServiceStub: SinonStubbedInstance<ShipmentService>;

    beforeEach(() => {
      route = {};
      shipmentServiceStub = sinon.createStubInstance<ShipmentService>(ShipmentService);
      shipmentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          shipmentService: () => shipmentServiceStub,
          shipmentStatusService: () =>
            sinon.createStubInstance<ShipmentStatusService>(ShipmentStatusService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          orderService: () =>
            sinon.createStubInstance<OrderService>(OrderService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          invoiceService: () =>
            sinon.createStubInstance<InvoiceService>(InvoiceService, {
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
        const wrapper = shallowMount(ShipmentUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ShipmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipment = shipmentSample;
        shipmentServiceStub.update.resolves(shipmentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentServiceStub.update.calledWith(shipmentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shipmentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShipmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipment = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        shipmentServiceStub.find.resolves(shipmentSample);
        shipmentServiceStub.retrieve.resolves([shipmentSample]);

        // WHEN
        route = {
          params: {
            shipmentId: '' + shipmentSample.id,
          },
        };
        const wrapper = shallowMount(ShipmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shipment).toMatchObject(shipmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentServiceStub.find.resolves(shipmentSample);
        const wrapper = shallowMount(ShipmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
