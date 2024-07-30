/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import InvoiceStatusUpdate from './invoice-status-update.vue';
import InvoiceStatusService from './invoice-status.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type InvoiceStatusUpdateComponentType = InstanceType<typeof InvoiceStatusUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const invoiceStatusSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InvoiceStatusUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('InvoiceStatus Management Update Component', () => {
    let comp: InvoiceStatusUpdateComponentType;
    let invoiceStatusServiceStub: SinonStubbedInstance<InvoiceStatusService>;

    beforeEach(() => {
      route = {};
      invoiceStatusServiceStub = sinon.createStubInstance<InvoiceStatusService>(InvoiceStatusService);
      invoiceStatusServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          invoiceStatusService: () => invoiceStatusServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(InvoiceStatusUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(InvoiceStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.invoiceStatus = invoiceStatusSample;
        invoiceStatusServiceStub.update.resolves(invoiceStatusSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(invoiceStatusServiceStub.update.calledWith(invoiceStatusSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        invoiceStatusServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InvoiceStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.invoiceStatus = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(invoiceStatusServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        invoiceStatusServiceStub.find.resolves(invoiceStatusSample);
        invoiceStatusServiceStub.retrieve.resolves([invoiceStatusSample]);

        // WHEN
        route = {
          params: {
            invoiceStatusId: '' + invoiceStatusSample.id,
          },
        };
        const wrapper = shallowMount(InvoiceStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.invoiceStatus).toMatchObject(invoiceStatusSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        invoiceStatusServiceStub.find.resolves(invoiceStatusSample);
        const wrapper = shallowMount(InvoiceStatusUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
