import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ParticipantService from './participant.service';
import { type IParticipant } from '@/shared/model/participant.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Participant',
  setup() {
    const dateFormat = useDateFormat();
    const participantService = inject('participantService', () => new ParticipantService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const participants: Ref<IParticipant[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveParticipants = async () => {
      isFetching.value = true;
      try {
        const res = await participantService().retrieve();
        participants.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveParticipants();
    };

    onMounted(async () => {
      await retrieveParticipants();
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IParticipant) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeParticipant = async () => {
      try {
        await participantService().delete(removeId.value);
        const message = 'A Participant is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveParticipants();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      participants,
      handleSyncList,
      isFetching,
      retrieveParticipants,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeParticipant,
    };
  },
});
