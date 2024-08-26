/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import WarehouseAssignmentUpdate from './warehouse-assignment-update.vue';
import WarehouseAssignmentService from './warehouse-assignment.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import WorkingUnitService from '@/entities/working-unit/working-unit.service';
import InternalOrderService from '@/entities/internal-order/internal-order.service';

type WarehouseAssignmentUpdateComponentType = InstanceType<typeof WarehouseAssignmentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const warehouseAssignmentSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<WarehouseAssignmentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('WarehouseAssignment Management Update Component', () => {
    let comp: WarehouseAssignmentUpdateComponentType;
    let warehouseAssignmentServiceStub: SinonStubbedInstance<WarehouseAssignmentService>;

    beforeEach(() => {
      route = {};
      warehouseAssignmentServiceStub = sinon.createStubInstance<WarehouseAssignmentService>(WarehouseAssignmentService);
      warehouseAssignmentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          warehouseAssignmentService: () => warehouseAssignmentServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          workingUnitService: () =>
            sinon.createStubInstance<WorkingUnitService>(WorkingUnitService, {
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
        const wrapper = shallowMount(WarehouseAssignmentUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(WarehouseAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.warehouseAssignment = warehouseAssignmentSample;
        warehouseAssignmentServiceStub.update.resolves(warehouseAssignmentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(warehouseAssignmentServiceStub.update.calledWith(warehouseAssignmentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        warehouseAssignmentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(WarehouseAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.warehouseAssignment = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(warehouseAssignmentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        warehouseAssignmentServiceStub.find.resolves(warehouseAssignmentSample);
        warehouseAssignmentServiceStub.retrieve.resolves([warehouseAssignmentSample]);

        // WHEN
        route = {
          params: {
            warehouseAssignmentId: '' + warehouseAssignmentSample.id,
          },
        };
        const wrapper = shallowMount(WarehouseAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.warehouseAssignment).toMatchObject(warehouseAssignmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        warehouseAssignmentServiceStub.find.resolves(warehouseAssignmentSample);
        const wrapper = shallowMount(WarehouseAssignmentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
