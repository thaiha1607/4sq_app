/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InternalOrder from './internal-order.vue';
import InternalOrderService from './internal-order.service';
import AlertService from '@/shared/alert/alert.service';

type InternalOrderComponentType = InstanceType<typeof InternalOrder>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InternalOrder Management Component', () => {
    let internalOrderServiceStub: SinonStubbedInstance<InternalOrderService>;
    let mountOptions: MountingOptions<InternalOrderComponentType>['global'];

    beforeEach(() => {
      internalOrderServiceStub = sinon.createStubInstance<InternalOrderService>(InternalOrderService);
      internalOrderServiceStub.retrieve.resolves({ headers: {} });

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
          internalOrderService: () => internalOrderServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        internalOrderServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(InternalOrder, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(internalOrderServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.internalOrders[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: InternalOrderComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InternalOrder, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        internalOrderServiceStub.retrieve.reset();
        internalOrderServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        internalOrderServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeInternalOrder();
        await comp.$nextTick(); // clear components

        // THEN
        expect(internalOrderServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(internalOrderServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
