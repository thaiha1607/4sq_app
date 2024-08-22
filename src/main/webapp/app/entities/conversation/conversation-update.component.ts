import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ConversationService from './conversation.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IConversation, Conversation } from '@/shared/model/conversation.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ConversationUpdate',
  setup() {
    const conversationService = inject('conversationService', () => new ConversationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const conversation: Ref<IConversation> = ref(new Conversation());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveConversation = async conversationId => {
      try {
        const res = await conversationService().find(conversationId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        conversation.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.conversationId) {
      retrieveConversation(route.params.conversationId);
    }

    const initRelationships = () => {};

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('This field is required.'),
        maxLength: validations.maxLength('This field cannot be longer than 100 characters.', 100),
      },
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      participants: {},
    };
    const v$ = useVuelidate(validationRules, conversation as any);
    v$.value.$validate();

    return {
      conversationService,
      alertService,
      conversation,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: conversation }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.conversation.id) {
        this.conversationService()
          .update(this.conversation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Conversation is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.conversationService()
          .create(this.conversation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Conversation is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
