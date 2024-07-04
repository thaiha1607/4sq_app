import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import WarehouseAssignmentService from './warehouse-assignment.service';
import { useDateFormat } from '@/shared/composables';
import { type IWarehouseAssignment } from '@/shared/model/warehouse-assignment.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'WarehouseAssignmentDetails',
  setup() {
    const dateFormat = useDateFormat();
    const warehouseAssignmentService = inject('warehouseAssignmentService', () => new WarehouseAssignmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const warehouseAssignment: Ref<IWarehouseAssignment> = ref({});

    const retrieveWarehouseAssignment = async warehouseAssignmentId => {
      try {
        const res = await warehouseAssignmentService().find(warehouseAssignmentId);
        warehouseAssignment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.warehouseAssignmentId) {
      retrieveWarehouseAssignment(route.params.warehouseAssignmentId);
    }

    return {
      ...dateFormat,
      alertService,
      warehouseAssignment,

      previousState,
    };
  },
});
