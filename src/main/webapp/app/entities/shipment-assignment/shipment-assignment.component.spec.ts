/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ShipmentAssignment from './shipment-assignment.vue';
import ShipmentAssignmentService from './shipment-assignment.service';
import AlertService from '@/shared/alert/alert.service';

type ShipmentAssignmentComponentType = InstanceType<typeof ShipmentAssignment>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ShipmentAssignment Management Component', () => {
    let shipmentAssignmentServiceStub: SinonStubbedInstance<ShipmentAssignmentService>;
    let mountOptions: MountingOptions<ShipmentAssignmentComponentType>['global'];

    beforeEach(() => {
      shipmentAssignmentServiceStub = sinon.createStubInstance<ShipmentAssignmentService>(ShipmentAssignmentService);
      shipmentAssignmentServiceStub.retrieve.resolves({ headers: {} });

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
          shipmentAssignmentService: () => shipmentAssignmentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shipmentAssignmentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(ShipmentAssignment, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(shipmentAssignmentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.shipmentAssignments[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ShipmentAssignmentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ShipmentAssignment, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        shipmentAssignmentServiceStub.retrieve.reset();
        shipmentAssignmentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        shipmentAssignmentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeShipmentAssignment();
        await comp.$nextTick(); // clear components

        // THEN
        expect(shipmentAssignmentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(shipmentAssignmentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
