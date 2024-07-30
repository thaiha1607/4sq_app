/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ShipmentStatus from './shipment-status.vue';
import ShipmentStatusService from './shipment-status.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentStatusComponentType = InstanceType<typeof ShipmentStatus>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ShipmentStatus Management Component', () => {
    let shipmentStatusServiceStub: SinonStubbedInstance<ShipmentStatusService>;
    let mountOptions: MountingOptions<ShipmentStatusComponentType>['global'];

    beforeEach(() => {
      shipmentStatusServiceStub = sinon.createStubInstance<ShipmentStatusService>(ShipmentStatusService);
      shipmentStatusServiceStub.retrieve.resolves({ headers: {} });

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
          shipmentStatusService: () => shipmentStatusServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentStatusServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(ShipmentStatus, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(shipmentStatusServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.shipmentStatuses[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ShipmentStatusComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ShipmentStatus, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        shipmentStatusServiceStub.retrieve.reset();
        shipmentStatusServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        shipmentStatusServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeShipmentStatus();
        await comp.$nextTick(); // clear components

        // THEN
        expect(shipmentStatusServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(shipmentStatusServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
