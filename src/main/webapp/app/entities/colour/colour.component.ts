import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ColourService from './colour.service';
import { type IColour } from '@/shared/model/colour.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Colour',
  setup() {
    const dateFormat = useDateFormat();
    const colourService = inject('colourService', () => new ColourService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const currentSearch = ref('');

    const colours: Ref<IColour[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      currentSearch.value = '';
    };

    const retrieveColours = async () => {
      isFetching.value = true;
      try {
        const res = currentSearch.value ? await colourService().search(currentSearch.value) : await colourService().retrieve();
        colours.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveColours();
    };

    onMounted(async () => {
      await retrieveColours();
    });

    const search = query => {
      if (!query) {
        return clear();
      }
      currentSearch.value = query;
      retrieveColours();
    };

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IColour) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeColour = async () => {
      try {
        await colourService().delete(removeId.value);
        const message = 'A Colour is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveColours();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      colours,
      handleSyncList,
      isFetching,
      retrieveColours,
      clear,
      ...dateFormat,
      currentSearch,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeColour,
    };
  },
});
