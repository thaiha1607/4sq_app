/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InvoiceDetails from './invoice-details.vue';
import InvoiceService from './invoice.service';
import AlertService from '@/shared/alert/alert.service';

type InvoiceDetailsComponentType = InstanceType<typeof InvoiceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const invoiceSample = { id: '9fec3727-3421-4967-b213-ba36557ca194' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Invoice Management Detail Component', () => {
    let invoiceServiceStub: SinonStubbedInstance<InvoiceService>;
    let mountOptions: MountingOptions<InvoiceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      invoiceServiceStub = sinon.createStubInstance<InvoiceService>(InvoiceService);

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          invoiceService: () => invoiceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        invoiceServiceStub.find.resolves(invoiceSample);
        route = {
          params: {
            invoiceId: '' + '9fec3727-3421-4967-b213-ba36557ca194',
          },
        };
        const wrapper = shallowMount(InvoiceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.invoice).toMatchObject(invoiceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        invoiceServiceStub.find.resolves(invoiceSample);
        const wrapper = shallowMount(InvoiceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
