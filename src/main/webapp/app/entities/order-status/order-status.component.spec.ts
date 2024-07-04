/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import OrderStatus from './order-status.vue';
import OrderStatusService from './order-status.service';
import AlertService from '@/shared/alert/alert.service';

type OrderStatusComponentType = InstanceType<typeof OrderStatus>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('OrderStatus Management Component', () => {
    let orderStatusServiceStub: SinonStubbedInstance<OrderStatusService>;
    let mountOptions: MountingOptions<OrderStatusComponentType>['global'];

    beforeEach(() => {
      orderStatusServiceStub = sinon.createStubInstance<OrderStatusService>(OrderStatusService);
      orderStatusServiceStub.retrieve.resolves({ headers: {} });

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
          orderStatusService: () => orderStatusServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderStatusServiceStub.retrieve.resolves({ headers: {}, data: [{ statusCode: 123 }] });

        // WHEN
        const wrapper = shallowMount(OrderStatus, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(orderStatusServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.orderStatuses[0]).toEqual(expect.objectContaining({ statusCode: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: OrderStatusComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(OrderStatus, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        orderStatusServiceStub.retrieve.reset();
        orderStatusServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        orderStatusServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ statusCode: 123 });

        comp.removeOrderStatus();
        await comp.$nextTick(); // clear components

        // THEN
        expect(orderStatusServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(orderStatusServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
