/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import StaffInfoUpdate from './staff-info-update.vue';
import StaffInfoService from './staff-info.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import WorkingUnitService from '@/entities/working-unit/working-unit.service';

type StaffInfoUpdateComponentType = InstanceType<typeof StaffInfoUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const staffInfoSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<StaffInfoUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('StaffInfo Management Update Component', () => {
    let comp: StaffInfoUpdateComponentType;
    let staffInfoServiceStub: SinonStubbedInstance<StaffInfoService>;

    beforeEach(() => {
      route = {};
      staffInfoServiceStub = sinon.createStubInstance<StaffInfoService>(StaffInfoService);
      staffInfoServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          staffInfoService: () => staffInfoServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          workingUnitService: () =>
            sinon.createStubInstance<WorkingUnitService>(WorkingUnitService, {
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
        const wrapper = shallowMount(StaffInfoUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(StaffInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.staffInfo = staffInfoSample;
        staffInfoServiceStub.update.resolves(staffInfoSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(staffInfoServiceStub.update.calledWith(staffInfoSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        staffInfoServiceStub.create.resolves(entity);
        const wrapper = shallowMount(StaffInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.staffInfo = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(staffInfoServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        staffInfoServiceStub.find.resolves(staffInfoSample);
        staffInfoServiceStub.retrieve.resolves([staffInfoSample]);

        // WHEN
        route = {
          params: {
            staffInfoId: '' + staffInfoSample.id,
          },
        };
        const wrapper = shallowMount(StaffInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.staffInfo).toMatchObject(staffInfoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        staffInfoServiceStub.find.resolves(staffInfoSample);
        const wrapper = shallowMount(StaffInfoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
