/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Colour from './colour.vue';
import ColourService from './colour.service';
import AlertService from '@/shared/alert/alert.service';

type ColourComponentType = InstanceType<typeof Colour>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Colour Management Component', () => {
    let colourServiceStub: SinonStubbedInstance<ColourService>;
    let mountOptions: MountingOptions<ColourComponentType>['global'];

    beforeEach(() => {
      colourServiceStub = sinon.createStubInstance<ColourService>(ColourService);
      colourServiceStub.retrieve.resolves({ headers: {} });

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
          colourService: () => colourServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        colourServiceStub.retrieve.resolves({ headers: {}, data: [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }] });

        // WHEN
        const wrapper = shallowMount(Colour, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(colourServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.colours[0]).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
      });
    });
    describe('Handles', () => {
      let comp: ColourComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Colour, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        colourServiceStub.retrieve.reset();
        colourServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        colourServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: '9fec3727-3421-4967-b213-ba36557ca194' });

        comp.removeColour();
        await comp.$nextTick(); // clear components

        // THEN
        expect(colourServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(colourServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
