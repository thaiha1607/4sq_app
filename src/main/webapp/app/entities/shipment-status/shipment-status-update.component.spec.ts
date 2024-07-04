/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ShipmentStatusUpdate from './shipment-status-update.vue';
import ShipmentStatusService from './shipment-status.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type ShipmentStatusUpdateComponentType = InstanceType<typeof ShipmentStatusUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentStatusSample = { statusCode: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShipmentStatusUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ShipmentStatus Management Update Component', () => {
    let comp: ShipmentStatusUpdateComponentType;
    let shipmentStatusServiceStub: SinonStubbedInstance<ShipmentStatusService>;

    beforeEach(() => {
      route = {};
      shipmentStatusServiceStub = sinon.createStubInstance<ShipmentStatusService>(ShipmentStatusService);
      shipmentStatusServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          shipmentStatusService: () => shipmentStatusServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ShipmentStatusUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ShipmentStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipmentStatus = shipmentStatusSample;
        shipmentStatusServiceStub.update.resolves(shipmentStatusSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentStatusServiceStub.update.calledWith(shipmentStatusSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shipmentStatusServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShipmentStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipmentStatus = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentStatusServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        shipmentStatusServiceStub.find.resolves(shipmentStatusSample);
        shipmentStatusServiceStub.retrieve.resolves([shipmentStatusSample]);

        // WHEN
        route = {
          params: {
            shipmentStatusId: '' + shipmentStatusSample.statusCode,
          },
        };
        const wrapper = shallowMount(ShipmentStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shipmentStatus).toMatchObject(shipmentStatusSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentStatusServiceStub.find.resolves(shipmentStatusSample);
        const wrapper = shallowMount(ShipmentStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
