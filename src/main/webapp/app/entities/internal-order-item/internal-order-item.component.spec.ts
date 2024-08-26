/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InternalOrderItem from './internal-order-item.vue';
import InternalOrderItemService from './internal-order-item.service';
import AlertService from '@/shared/alert/alert.service';

type InternalOrderItemComponentType = InstanceType<typeof InternalOrderItem>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InternalOrderItem Management Component', () => {
    let internalOrderItemServiceStub: SinonStubbedInstance<InternalOrderItemService>;
    let mountOptions: MountingOptions<InternalOrderItemComponentType>['global'];

    beforeEach(() => {
      internalOrderItemServiceStub = sinon.createStubInstance<InternalOrderItemService>(InternalOrderItemService);
      internalOrderItemServiceStub.retrieve.resolves({ headers: {} });

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
          internalOrderItemService: () => internalOrderItemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        internalOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(InternalOrderItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(internalOrderItemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.internalOrderItems[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: InternalOrderItemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InternalOrderItem, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        internalOrderItemServiceStub.retrieve.reset();
        internalOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        internalOrderItemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeInternalOrderItem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(internalOrderItemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(internalOrderItemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
