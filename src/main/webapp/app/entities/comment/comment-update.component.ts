import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CommentService from './comment.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import ProductService from '@/entities/product/product.service';
import { type IProduct } from '@/shared/model/product.model';
import { type IComment, Comment } from '@/shared/model/comment.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CommentUpdate',
  setup() {
    const commentService = inject('commentService', () => new CommentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const comment: Ref<IComment> = ref(new Comment());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const productService = inject('productService', () => new ProductService());

    const products: Ref<IProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveComment = async commentId => {
      try {
        const res = await commentService().find(commentId);
        res.createdDate = new Date(res.createdDate);
        res.lastModifiedDate = new Date(res.lastModifiedDate);
        comment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.commentId) {
      retrieveComment(route.params.commentId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      productService()
        .retrieve()
        .then(res => {
          products.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      rating: {
        required: validations.required('This field is required.'),
        integer: validations.integer('This field should be a number.'),
        min: validations.minValue('This field should be at least 1.', 1),
        max: validations.maxValue('This field cannot be more than 5.', 5),
      },
      content: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
      user: {
        required: validations.required('This field is required.'),
      },
      product: {
        required: validations.required('This field is required.'),
      },
    };
    const v$ = useVuelidate(validationRules, comment as any);
    v$.value.$validate();

    return {
      commentService,
      alertService,
      comment,
      previousState,
      isSaving,
      currentLanguage,
      users,
      products,
      v$,
      ...useDateFormat({ entityRef: comment }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.comment.id) {
        this.commentService()
          .update(this.comment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Comment is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.commentService()
          .create(this.comment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Comment is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
