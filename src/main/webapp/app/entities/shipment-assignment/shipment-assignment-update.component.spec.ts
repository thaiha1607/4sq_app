/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ShipmentAssignmentUpdate from './shipment-assignment-update.vue';
import ShipmentAssignmentService from './shipment-assignment.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import ShipmentService from '@/entities/shipment/shipment.service';

type ShipmentAssignmentUpdateComponentType = InstanceType<typeof ShipmentAssignmentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shipmentAssignmentSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShipmentAssignmentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ShipmentAssignment Management Update Component', () => {
    let comp: ShipmentAssignmentUpdateComponentType;
    let shipmentAssignmentServiceStub: SinonStubbedInstance<ShipmentAssignmentService>;

    beforeEach(() => {
      route = {};
      shipmentAssignmentServiceStub = sinon.createStubInstance<ShipmentAssignmentService>(ShipmentAssignmentService);
      shipmentAssignmentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          shipmentAssignmentService: () => shipmentAssignmentServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
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
        const wrapper = shallowMount(ShipmentAssignmentUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ShipmentAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipmentAssignment = shipmentAssignmentSample;
        shipmentAssignmentServiceStub.update.resolves(shipmentAssignmentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentAssignmentServiceStub.update.calledWith(shipmentAssignmentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shipmentAssignmentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShipmentAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shipmentAssignment = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shipmentAssignmentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        shipmentAssignmentServiceStub.find.resolves(shipmentAssignmentSample);
        shipmentAssignmentServiceStub.retrieve.resolves([shipmentAssignmentSample]);

        // WHEN
        route = {
          params: {
            shipmentAssignmentId: '' + shipmentAssignmentSample.id,
          },
        };
        const wrapper = shallowMount(ShipmentAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shipmentAssignment).toMatchObject(shipmentAssignmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shipmentAssignmentServiceStub.find.resolves(shipmentAssignmentSample);
        const wrapper = shallowMount(ShipmentAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
