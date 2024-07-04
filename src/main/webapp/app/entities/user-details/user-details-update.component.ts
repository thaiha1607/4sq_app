import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import UserDetailsService from './user-details.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import WorkingUnitService from '@/entities/working-unit/working-unit.service';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import { type IUserDetails, UserDetails } from '@/shared/model/user-details.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserDetailsUpdate',
  setup() {
    const userDetailsService = inject('userDetailsService', () => new UserDetailsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const userDetails: Ref<IUserDetails> = ref(new UserDetails());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const workingUnitService = inject('workingUnitService', () => new WorkingUnitService());

    const workingUnits: Ref<IWorkingUnit[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveUserDetails = async userDetailsId => {
      try {
        const res = await userDetailsService().find(userDetailsId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        userDetails.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userDetailsId) {
      retrieveUserDetails(route.params.userDetailsId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      workingUnitService()
        .retrieve()
        .then(res => {
          workingUnits.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      phone: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {
        required: validations.required('This field is required.'),
      },
      workingUnit: {},
    };
    const v$ = useVuelidate(validationRules, userDetails as any);
    v$.value.$validate();

    return {
      userDetailsService,
      alertService,
      userDetails,
      previousState,
      isSaving,
      currentLanguage,
      users,
      workingUnits,
      v$,
      ...useDateFormat({ entityRef: userDetails }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.userDetails.id) {
        this.userDetailsService()
          .update(this.userDetails)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A UserDetails is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.userDetailsService()
          .create(this.userDetails)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A UserDetails is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
