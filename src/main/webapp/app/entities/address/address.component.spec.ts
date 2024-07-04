/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Address from './address.vue';
import AddressService from './address.service';
import AlertService from '@/shared/alert/alert.service';

type AddressComponentType = InstanceType<typeof Address>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Address Management Component', () => {
    let addressServiceStub: SinonStubbedInstance<AddressService>;
    let mountOptions: MountingOptions<AddressComponentType>['global'];

    beforeEach(() => {
      addressServiceStub = sinon.createStubInstance<AddressService>(AddressService);
      addressServiceStub.retrieve.resolves({ headers: {} });

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
          addressService: () => addressServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        addressServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Address, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(addressServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.addresses[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: AddressComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Address, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        addressServiceStub.retrieve.reset();
        addressServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        addressServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeAddress();
        await comp.$nextTick(); // clear components

        // THEN
        expect(addressServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(addressServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
