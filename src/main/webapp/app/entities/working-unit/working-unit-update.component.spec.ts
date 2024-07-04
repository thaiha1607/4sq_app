/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import WorkingUnitUpdate from './working-unit-update.vue';
import WorkingUnitService from './working-unit.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import AddressService from '@/entities/address/address.service';

type WorkingUnitUpdateComponentType = InstanceType<typeof WorkingUnitUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const workingUnitSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<WorkingUnitUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('WorkingUnit Management Update Component', () => {
    let comp: WorkingUnitUpdateComponentType;
    let workingUnitServiceStub: SinonStubbedInstance<WorkingUnitService>;

    beforeEach(() => {
      route = {};
      workingUnitServiceStub = sinon.createStubInstance<WorkingUnitService>(WorkingUnitService);
      workingUnitServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          workingUnitService: () => workingUnitServiceStub,
          addressService: () =>
            sinon.createStubInstance<AddressService>(AddressService, {
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
        const wrapper = shallowMount(WorkingUnitUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(WorkingUnitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.workingUnit = workingUnitSample;
        workingUnitServiceStub.update.resolves(workingUnitSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(workingUnitServiceStub.update.calledWith(workingUnitSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        workingUnitServiceStub.create.resolves(entity);
        const wrapper = shallowMount(WorkingUnitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.workingUnit = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(workingUnitServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        workingUnitServiceStub.find.resolves(workingUnitSample);
        workingUnitServiceStub.retrieve.resolves([workingUnitSample]);

        // WHEN
        route = {
          params: {
            workingUnitId: '' + workingUnitSample.id,
          },
        };
        const wrapper = shallowMount(WorkingUnitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.workingUnit).toMatchObject(workingUnitSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        workingUnitServiceStub.find.resolves(workingUnitSample);
        const wrapper = shallowMount(WorkingUnitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
