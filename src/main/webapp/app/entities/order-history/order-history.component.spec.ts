/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import OrderHistory from './order-history.vue';
import OrderHistoryService from './order-history.service';
import AlertService from '@/shared/alert/alert.service';

type OrderHistoryComponentType = InstanceType<typeof OrderHistory>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('OrderHistory Management Component', () => {
    let orderHistoryServiceStub: SinonStubbedInstance<OrderHistoryService>;
    let mountOptions: MountingOptions<OrderHistoryComponentType>['global'];

    beforeEach(() => {
      orderHistoryServiceStub = sinon.createStubInstance<OrderHistoryService>(OrderHistoryService);
      orderHistoryServiceStub.retrieve.resolves({ headers: {} });

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
          orderHistoryService: () => orderHistoryServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderHistoryServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(OrderHistory, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(orderHistoryServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.orderHistories[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: OrderHistoryComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(OrderHistory, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        orderHistoryServiceStub.retrieve.reset();
        orderHistoryServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        orderHistoryServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeOrderHistory();
        await comp.$nextTick(); // clear components

        // THEN
        expect(orderHistoryServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(orderHistoryServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
