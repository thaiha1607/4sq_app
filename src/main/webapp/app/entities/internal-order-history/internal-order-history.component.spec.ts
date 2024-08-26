/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InternalOrderHistory from './internal-order-history.vue';
import InternalOrderHistoryService from './internal-order-history.service';
import AlertService from '@/shared/alert/alert.service';

type InternalOrderHistoryComponentType = InstanceType<typeof InternalOrderHistory>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InternalOrderHistory Management Component', () => {
    let internalOrderHistoryServiceStub: SinonStubbedInstance<InternalOrderHistoryService>;
    let mountOptions: MountingOptions<InternalOrderHistoryComponentType>['global'];

    beforeEach(() => {
      internalOrderHistoryServiceStub = sinon.createStubInstance<InternalOrderHistoryService>(InternalOrderHistoryService);
      internalOrderHistoryServiceStub.retrieve.resolves({ headers: {} });

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
          internalOrderHistoryService: () => internalOrderHistoryServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        internalOrderHistoryServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(InternalOrderHistory, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(internalOrderHistoryServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.internalOrderHistories[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: InternalOrderHistoryComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InternalOrderHistory, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        internalOrderHistoryServiceStub.retrieve.reset();
        internalOrderHistoryServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        internalOrderHistoryServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeInternalOrderHistory();
        await comp.$nextTick(); // clear components

        // THEN
        expect(internalOrderHistoryServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(internalOrderHistoryServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
