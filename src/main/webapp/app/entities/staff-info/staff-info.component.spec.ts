/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import StaffInfo from './staff-info.vue';
import StaffInfoService from './staff-info.service';
import AlertService from '@/shared/alert/alert.service';

type StaffInfoComponentType = InstanceType<typeof StaffInfo>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('StaffInfo Management Component', () => {
    let staffInfoServiceStub: SinonStubbedInstance<StaffInfoService>;
    let mountOptions: MountingOptions<StaffInfoComponentType>['global'];

    beforeEach(() => {
      staffInfoServiceStub = sinon.createStubInstance<StaffInfoService>(StaffInfoService);
      staffInfoServiceStub.retrieve.resolves({ headers: {} });

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
          staffInfoService: () => staffInfoServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        staffInfoServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(StaffInfo, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(staffInfoServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.staffInfos[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: StaffInfoComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(StaffInfo, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        staffInfoServiceStub.retrieve.reset();
        staffInfoServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        staffInfoServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeStaffInfo();
        await comp.$nextTick(); // clear components

        // THEN
        expect(staffInfoServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(staffInfoServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
