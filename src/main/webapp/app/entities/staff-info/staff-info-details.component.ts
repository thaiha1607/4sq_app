import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import StaffInfoService from './staff-info.service';
import { useDateFormat } from '@/shared/composables';
import { type IStaffInfo } from '@/shared/model/staff-info.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'StaffInfoDetails',
  setup() {
    const dateFormat = useDateFormat();
    const staffInfoService = inject('staffInfoService', () => new StaffInfoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const staffInfo: Ref<IStaffInfo> = ref({});

    const retrieveStaffInfo = async staffInfoId => {
      try {
        const res = await staffInfoService().find(staffInfoId);
        staffInfo.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.staffInfoId) {
      retrieveStaffInfo(route.params.staffInfoId);
    }

    return {
      ...dateFormat,
      alertService,
      staffInfo,

      previousState,
    };
  },
});
