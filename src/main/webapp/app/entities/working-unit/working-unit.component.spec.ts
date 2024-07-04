/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import WorkingUnit from './working-unit.vue';
import WorkingUnitService from './working-unit.service';
import AlertService from '@/shared/alert/alert.service';

type WorkingUnitComponentType = InstanceType<typeof WorkingUnit>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('WorkingUnit Management Component', () => {
    let workingUnitServiceStub: SinonStubbedInstance<WorkingUnitService>;
    let mountOptions: MountingOptions<WorkingUnitComponentType>['global'];

    beforeEach(() => {
      workingUnitServiceStub = sinon.createStubInstance<WorkingUnitService>(WorkingUnitService);
      workingUnitServiceStub.retrieve.resolves({ headers: {} });

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
          workingUnitService: () => workingUnitServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        workingUnitServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(WorkingUnit, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(workingUnitServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.workingUnits[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: WorkingUnitComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(WorkingUnit, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        workingUnitServiceStub.retrieve.reset();
        workingUnitServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        workingUnitServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeWorkingUnit();
        await comp.$nextTick(); // clear components

        // THEN
        expect(workingUnitServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(workingUnitServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
