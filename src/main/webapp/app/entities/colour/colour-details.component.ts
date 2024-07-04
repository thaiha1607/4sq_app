import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ColourService from './colour.service';
import { useDateFormat } from '@/shared/composables';
import { type IColour } from '@/shared/model/colour.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ColourDetails',
  setup() {
    const dateFormat = useDateFormat();
    const colourService = inject('colourService', () => new ColourService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const colour: Ref<IColour> = ref({});

    const retrieveColour = async colourId => {
      try {
        const res = await colourService().find(colourId);
        colour.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.colourId) {
      retrieveColour(route.params.colourId);
    }

    return {
      ...dateFormat,
      alertService,
      colour,

      previousState,
    };
  },
});
