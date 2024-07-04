/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Shipment from './shipment.vue';
import ShipmentService from './shipment.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentComponentType = InstanceType<typeof Shipment>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Shipment Management Component', () => {
    let shipmentServiceStub: SinonStubbedInstance<ShipmentService>;
    let mountOptions: MountingOptions<ShipmentComponentType>['global'];

    beforeEach(() => {
      shipmentServiceStub = sinon.createStubInstance<ShipmentService>(ShipmentService);
      shipmentServiceStub.retrieve.resolves({ headers: {} });

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
          shipmentService: () => shipmentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Shipment, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(shipmentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.shipments[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ShipmentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Shipment, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        shipmentServiceStub.retrieve.reset();
        shipmentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        shipmentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeShipment();
        await comp.$nextTick(); // clear components

        // THEN
        expect(shipmentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(shipmentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
