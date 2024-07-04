/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InvoiceStatusDetails from './invoice-status-details.vue';
import InvoiceStatusService from './invoice-status.service';
import AlertService from '@/shared/alert/alert.service';

type InvoiceStatusDetailsComponentType = InstanceType<typeof InvoiceStatusDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const invoiceStatusSample = { statusCode: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('InvoiceStatus Management Detail Component', () => {
    let invoiceStatusServiceStub: SinonStubbedInstance<InvoiceStatusService>;
    let mountOptions: MountingOptions<InvoiceStatusDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      invoiceStatusServiceStub = sinon.createStubInstance<InvoiceStatusService>(InvoiceStatusService);

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
          invoiceStatusService: () => invoiceStatusServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        invoiceStatusServiceStub.find.resolves(invoiceStatusSample);
        route = {
          params: {
            invoiceStatusId: '' + 123,
          },
        };
        const wrapper = shallowMount(InvoiceStatusDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.invoiceStatus).toMatchObject(invoiceStatusSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        invoiceStatusServiceStub.find.resolves(invoiceStatusSample);
        const wrapper = shallowMount(InvoiceStatusDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
