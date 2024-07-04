/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ColourUpdate from './colour-update.vue';
import ColourService from './colour.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type ColourUpdateComponentType = InstanceType<typeof ColourUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const colourSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ColourUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Colour Management Update Component', () => {
    let comp: ColourUpdateComponentType;
    let colourServiceStub: SinonStubbedInstance<ColourService>;

    beforeEach(() => {
      route = {};
      colourServiceStub = sinon.createStubInstance<ColourService>(ColourService);
      colourServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          colourService: () => colourServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ColourUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ColourUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.colour = colourSample;
        colourServiceStub.update.resolves(colourSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(colourServiceStub.update.calledWith(colourSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        colourServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ColourUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.colour = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(colourServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        colourServiceStub.find.resolves(colourSample);
        colourServiceStub.retrieve.resolves([colourSample]);

        // WHEN
        route = {
          params: {
            colourId: '' + colourSample.id,
          },
        };
        const wrapper = shallowMount(ColourUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.colour).toMatchObject(colourSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        colourServiceStub.find.resolves(colourSample);
        const wrapper = shallowMount(ColourUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
