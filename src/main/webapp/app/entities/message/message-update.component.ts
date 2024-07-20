import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import MessageService from './message.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ParticipantService from '@/entities/participant/participant.service';
import { type IParticipant } from '@/shared/model/participant.model';
import { type IMessage, Message } from '@/shared/model/message.model';
import { MessageType } from '@/shared/model/enumerations/message-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'MessageUpdate',
  setup() {
    const messageService = inject('messageService', () => new MessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const message: Ref<IMessage> = ref(new Message());

    const participantService = inject('participantService', () => new ParticipantService());

    const participants: Ref<IParticipant[]> = ref([]);
    const messageTypeValues: Ref<string[]> = ref(Object.keys(MessageType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveMessage = async messageId => {
      try {
        const res = await messageService().find(messageId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        message.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.messageId) {
      retrieveMessage(route.params.messageId);
    }

    const initRelationships = () => {
      participantService()
        .retrieve()
        .then(res => {
          participants.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      type: {
        required: validations.required('This field is required.'),
      },
      content: {
        required: validations.required('This field is required.'),
      },
      isSeen: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      participant: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, message as any);
    v$.value.$validate();

    return {
      messageService,
      alertService,
      message,
      previousState,
      messageTypeValues,
      isSaving,
      currentLanguage,
      participants,
      v$,
      ...useDateFormat({ entityRef: message }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.message.id) {
        this.messageService()
          .update(this.message)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Message is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.messageService()
          .create(this.message)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Message is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
