import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ColourService from './colour.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IColour, Colour } from '@/shared/model/colour.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ColourUpdate',
  setup() {
    const colourService = inject('colourService', () => new ColourService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const colour: Ref<IColour> = ref(new Colour());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveColour = async colourId => {
      try {
        const res = await colourService().find(colourId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        colour.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.colourId) {
      retrieveColour(route.params.colourId);
    }

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('This field is required.'),
      },
      hexCode: {
        required: validations.required('This field is required.'),
      },
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
    };
    const v$ = useVuelidate(validationRules, colour as any);
    v$.value.$validate();

    return {
      colourService,
      alertService,
      colour,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: colour }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.colour.id) {
        this.colourService()
          .update(this.colour)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Colour is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.colourService()
          .create(this.colour)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Colour is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
