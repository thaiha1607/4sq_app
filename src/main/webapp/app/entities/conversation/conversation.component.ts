import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ConversationService from './conversation.service';
import { type IConversation } from '@/shared/model/conversation.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Conversation',
  setup() {
    const dateFormat = useDateFormat();
    const conversationService = inject('conversationService', () => new ConversationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const conversations: Ref<IConversation[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveConversations = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await conversationService().search(currentSearch.value) : await conversationService().retrieve();
        conversations.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveConversations();
    };

    onMounted(async () => {
      await retrieveConversations();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveConversations();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IConversation) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeConversation = async () => {
      try {
        await conversationService().delete(removeId.value);
        const message = 'A Conversation is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveConversations();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      conversations,
      handleSyncList,
      isFetching,
      retrieveConversations,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeConversation,
    };
  },
});
