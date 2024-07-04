/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ShipmentItem from './shipment-item.vue';
import ShipmentItemService from './shipment-item.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentItemComponentType = InstanceType<typeof ShipmentItem>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ShipmentItem Management Component', () => {
    let shipmentItemServiceStub: SinonStubbedInstance<ShipmentItemService>;
    let mountOptions: MountingOptions<ShipmentItemComponentType>['global'];

    beforeEach(() => {
      shipmentItemServiceStub = sinon.createStubInstance<ShipmentItemService>(ShipmentItemService);
      shipmentItemServiceStub.retrieve.resolves({ headers: {} });

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
          shipmentItemService: () => shipmentItemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(ShipmentItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(shipmentItemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.shipmentItems[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ShipmentItemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ShipmentItem, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        shipmentItemServiceStub.retrieve.reset();
        shipmentItemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        shipmentItemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeShipmentItem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(shipmentItemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(shipmentItemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
