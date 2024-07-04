/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import InvoiceUpdate from './invoice-update.vue';
import InvoiceService from './invoice.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import InvoiceStatusService from '@/entities/invoice-status/invoice-status.service';
import OrderService from '@/entities/order/order.service';

type InvoiceUpdateComponentType = InstanceType<typeof InvoiceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const invoiceSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<InvoiceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Invoice Management Update Component', () => {
    let comp: InvoiceUpdateComponentType;
    let invoiceServiceStub: SinonStubbedInstance<InvoiceService>;

    beforeEach(() => {
      route = {};
      invoiceServiceStub = sinon.createStubInstance<InvoiceService>(InvoiceService);
      invoiceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          invoiceService: () => invoiceServiceStub,
          invoiceStatusService: () =>
            sinon.createStubInstance<InvoiceStatusService>(InvoiceStatusService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          orderService: () =>
            sinon.createStubInstance<OrderService>(OrderService, {
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
        const wrapper = shallowMount(InvoiceUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(InvoiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.invoice = invoiceSample;
        invoiceServiceStub.update.resolves(invoiceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(invoiceServiceStub.update.calledWith(invoiceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        invoiceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(InvoiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.invoice = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(invoiceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        invoiceServiceStub.find.resolves(invoiceSample);
        invoiceServiceStub.retrieve.resolves([invoiceSample]);

        // WHEN
        route = {
          params: {
            invoiceId: '' + invoiceSample.id,
          },
        };
        const wrapper = shallowMount(InvoiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.invoice).toMatchObject(invoiceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        invoiceServiceStub.find.resolves(invoiceSample);
        const wrapper = shallowMount(InvoiceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
