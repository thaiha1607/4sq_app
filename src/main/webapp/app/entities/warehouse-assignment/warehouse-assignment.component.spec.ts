/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import WarehouseAssignment from './warehouse-assignment.vue';
import WarehouseAssignmentService from './warehouse-assignment.service';
import AlertService from '@/shared/alert/alert.service';

type WarehouseAssignmentComponentType = InstanceType<typeof WarehouseAssignment>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('WarehouseAssignment Management Component', () => {
    let warehouseAssignmentServiceStub: SinonStubbedInstance<WarehouseAssignmentService>;
    let mountOptions: MountingOptions<WarehouseAssignmentComponentType>['global'];

    beforeEach(() => {
      warehouseAssignmentServiceStub = sinon.createStubInstance<WarehouseAssignmentService>(WarehouseAssignmentService);
      warehouseAssignmentServiceStub.retrieve.resolves({ headers: {} });

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
          warehouseAssignmentService: () => warehouseAssignmentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        warehouseAssignmentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(WarehouseAssignment, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(warehouseAssignmentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.warehouseAssignments[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: WarehouseAssignmentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(WarehouseAssignment, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        warehouseAssignmentServiceStub.retrieve.reset();
        warehouseAssignmentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        warehouseAssignmentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeWarehouseAssignment();
        await comp.$nextTick(); // clear components

        // THEN
        expect(warehouseAssignmentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(warehouseAssignmentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
