import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import CommentService from './comment.service';
import { type IComment } from '@/shared/model/comment.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Comment',
  setup() {
    const dateFormat = useDateFormat();
    const commentService = inject('commentService', () => new CommentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const comments: Ref<IComment[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveComments = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await commentService().search(currentSearch.value) : await commentService().retrieve();
        comments.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveComments();
    };

    onMounted(async () => {
      await retrieveComments();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveComments();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IComment) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeComment = async () => {
      try {
        await commentService().delete(removeId.value);
        const message = 'A Comment is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveComments();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      comments,
      handleSyncList,
      isFetching,
      retrieveComments,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeComment,
    };
  },
});
