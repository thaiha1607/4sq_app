/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import OrderItem from './order-item.vue';
import OrderItemService from './order-item.service';
import AlertService from '@/shared/alert/alert.service';

type OrderItemComponentType = InstanceType<typeof OrderItem>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('OrderItem Management Component', () => {
    let orderItemServiceStub: SinonStubbedInstance<OrderItemService>;
    let mountOptions: MountingOptions<OrderItemComponentType>['global'];

    beforeEach(() => {
      orderItemServiceStub = sinon.createStubInstance<OrderItemService>(OrderItemService);
      orderItemServiceStub.retrieve.resolves({ headers: {} });

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
          orderItemService: () => orderItemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(OrderItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(orderItemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.orderItems[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: OrderItemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(OrderItem, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        orderItemServiceStub.retrieve.reset();
        orderItemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        orderItemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeOrderItem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(orderItemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(orderItemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
