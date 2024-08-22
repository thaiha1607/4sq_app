import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ParticipantService from './participant.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import ConversationService from '@/entities/conversation/conversation.service';
import { type IConversation } from '@/shared/model/conversation.model';
import MessageService from '@/entities/message/message.service';
import { type IMessage } from '@/shared/model/message.model';
import { type IParticipant, Participant } from '@/shared/model/participant.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ParticipantUpdate',
  setup() {
    const participantService = inject('participantService', () => new ParticipantService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const participant: Ref<IParticipant> = ref(new Participant());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const conversationService = inject('conversationService', () => new ConversationService());

    const conversations: Ref<IConversation[]> = ref([]);

    const messageService = inject('messageService', () => new MessageService());

    const messages: Ref<IMessage[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveParticipant = async participantId => {
      try {
        const res = await participantService().find(participantId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        participant.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.participantId) {
      retrieveParticipant(route.params.participantId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      conversationService()
        .retrieve()
        .then(res => {
          conversations.value = res.data;
        });
      messageService()
        .retrieve()
        .then(res => {
          messages.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      isAdmin: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {
        required: validations.required('This field is required.'),
      },
      conversation: {
        required: validations.required('This field is required.'),
      },
      messages: {},
      seenMessages: {},
    };
    const v$ = useVuelidate(validationRules, participant as any);
    v$.value.$validate();

    return {
      participantService,
      alertService,
      participant,
      previousState,
      isSaving,
      currentLanguage,
      users,
      conversations,
      messages,
      v$,
      ...useDateFormat({ entityRef: participant }),
    };
  },
  created(): void {
    this.participant.seenMessages = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.participant.id) {
        this.participantService()
          .update(this.participant)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Participant is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.participantService()
          .create(this.participant)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Participant is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
