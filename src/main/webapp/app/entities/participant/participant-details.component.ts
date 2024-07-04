import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ParticipantService from './participant.service';
import { useDateFormat } from '@/shared/composables';
import { type IParticipant } from '@/shared/model/participant.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ParticipantDetails',
  setup() {
    const dateFormat = useDateFormat();
    const participantService = inject('participantService', () => new ParticipantService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const participant: Ref<IParticipant> = ref({});

    const retrieveParticipant = async participantId => {
      try {
        const res = await participantService().find(participantId);
        participant.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.participantId) {
      retrieveParticipant(route.params.participantId);
    }

    return {
      ...dateFormat,
      alertService,
      participant,

      previousState,
    };
  },
});
