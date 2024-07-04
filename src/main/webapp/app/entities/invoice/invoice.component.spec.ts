/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Invoice from './invoice.vue';
import InvoiceService from './invoice.service';
import AlertService from '@/shared/alert/alert.service';

type InvoiceComponentType = InstanceType<typeof Invoice>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Invoice Management Component', () => {
    let invoiceServiceStub: SinonStubbedInstance<InvoiceService>;
    let mountOptions: MountingOptions<InvoiceComponentType>['global'];

    beforeEach(() => {
      invoiceServiceStub = sinon.createStubInstance<InvoiceService>(InvoiceService);
      invoiceServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          invoiceService: () => invoiceServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        invoiceServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Invoice, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(invoiceServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.invoices[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: InvoiceComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Invoice, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        invoiceServiceStub.retrieve.reset();
        invoiceServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        invoiceServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeInvoice();
        await comp.$nextTick(); // clear components

        // THEN
        expect(invoiceServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(invoiceServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
