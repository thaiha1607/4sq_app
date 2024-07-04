/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Order from './order.vue';
import OrderService from './order.service';
import AlertService from '@/shared/alert/alert.service';

type OrderComponentType = InstanceType<typeof Order>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Order Management Component', () => {
    let orderServiceStub: SinonStubbedInstance<OrderService>;
    let mountOptions: MountingOptions<OrderComponentType>['global'];

    beforeEach(() => {
      orderServiceStub = sinon.createStubInstance<OrderService>(OrderService);
      orderServiceStub.retrieve.resolves({ headers: {} });

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
          orderService: () => orderServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Order, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(orderServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.orders[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: OrderComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Order, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        orderServiceStub.retrieve.reset();
        orderServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        orderServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeOrder();
        await comp.$nextTick(); // clear components

        // THEN
        expect(orderServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(orderServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
