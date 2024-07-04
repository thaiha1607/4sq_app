import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import MessageService from './message.service';
import { type IMessage } from '@/shared/model/message.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Message',
  setup() {
    const dateFormat = useDateFormat();
    const messageService = inject('messageService', () => new MessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const messages: Ref<IMessage[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveMessages = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await messageService().search(currentSearch.value) : await messageService().retrieve();
        messages.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveMessages();
    };

    onMounted(async () => {
      await retrieveMessages();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveMessages();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IMessage) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeMessage = async () => {
      try {
        await messageService().delete(removeId.value);
        const message = 'A Message is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveMessages();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      messages,
      handleSyncList,
      isFetching,
      retrieveMessages,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeMessage,
    };
  },
});
