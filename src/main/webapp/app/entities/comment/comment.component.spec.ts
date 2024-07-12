/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Comment from './comment.vue';
import CommentService from './comment.service';
import AlertService from '@/shared/alert/alert.service';

type CommentComponentType = InstanceType<typeof Comment>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Comment Management Component', () => {
    let commentServiceStub: SinonStubbedInstance<CommentService>;
    let mountOptions: MountingOptions<CommentComponentType>['global'];

    beforeEach(() => {
      commentServiceStub = sinon.createStubInstance<CommentService>(CommentService);
      commentServiceStub.retrieve.resolves({ headers: {} });

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
          commentService: () => commentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        commentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Comment, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(commentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.comments[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CommentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Comment, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        commentServiceStub.retrieve.reset();
        commentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        commentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeComment();
        await comp.$nextTick(); // clear components

        // THEN
        expect(commentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(commentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
