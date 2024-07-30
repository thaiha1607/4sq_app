/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InvoiceStatus from './invoice-status.vue';
import InvoiceStatusService from './invoice-status.service';
import AlertService from '@/shared/alert/alert.service';

type InvoiceStatusComponentType = InstanceType<typeof InvoiceStatus>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InvoiceStatus Management Component', () => {
    let invoiceStatusServiceStub: SinonStubbedInstance<InvoiceStatusService>;
    let mountOptions: MountingOptions<InvoiceStatusComponentType>['global'];

    beforeEach(() => {
      invoiceStatusServiceStub = sinon.createStubInstance<InvoiceStatusService>(InvoiceStatusService);
      invoiceStatusServiceStub.retrieve.resolves({ headers: {} });

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
          invoiceStatusService: () => invoiceStatusServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        invoiceStatusServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(InvoiceStatus, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(invoiceStatusServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.invoiceStatuses[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: InvoiceStatusComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InvoiceStatus, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        invoiceStatusServiceStub.retrieve.reset();
        invoiceStatusServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        invoiceStatusServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeInvoiceStatus();
        await comp.$nextTick(); // clear components

        // THEN
        expect(invoiceStatusServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(invoiceStatusServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
