import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import WorkingUnitService from './working-unit.service';
import { useDateFormat } from '@/shared/composables';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'WorkingUnitDetails',
  setup() {
    const dateFormat = useDateFormat();
    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const workingUnit: Ref<IWorkingUnit> = ref({});

    const retrieveWorkingUnit = async workingUnitId => {
      try {
        const res = await workingUnitService().find(workingUnitId);
        workingUnit.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.workingUnitId) {
      retrieveWorkingUnit(route.params.workingUnitId);
    }

    return {
      ...dateFormat,
      alertService,
      workingUnit,

      previousState,
    };
  },
});
