import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import TagService from './tag.service';
import { type ITag } from '@/shared/model/tag.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Tag',
  setup() {
    const dateFormat = useDateFormat();
    const tagService = inject('tagService', () => new TagService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const tags: Ref<ITag[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveTags = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await tagService().search(currentSearch.value) : await tagService().retrieve();
        tags.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTags();
    };

    onMounted(async () => {
      await retrieveTags();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveTags();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITag) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTag = async () => {
      try {
        await tagService().delete(removeId.value);
        const message = 'A Tag is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTags();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      tags,
      handleSyncList,
      isFetching,
      retrieveTags,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTag,
    };
  },
});
