<template>
  <div>
    <h2 id="page-heading" data-cy="ProductCategoryHeading">
      <span id="product-category-heading">Product Categories</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ProductCategoryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-product-category"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Product Category</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && productCategories && productCategories.length === 0">
      <span>No Product Categories found</span>
    </div>
    <div class="table-responsive" v-if="productCategories && productCategories.length > 0">
      <table class="table table-striped" aria-describedby="productCategories">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Colour</span></th>
            <th scope="row"><span>Product</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="productCategory in productCategories" :key="productCategory.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ProductCategoryView', params: { productCategoryId: productCategory.id } }">{{
                productCategory.id
              }}</router-link>
            </td>
            <td>{{ productCategory.name }}</td>
            <td>{{ productCategory.createdBy }}</td>
            <td>{{ formatDateShort(productCategory.createdDate) || '' }}</td>
            <td>{{ productCategory.lastModifiedBy }}</td>
            <td>{{ formatDateShort(productCategory.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="productCategory.colour">
                <router-link :to="{ name: 'ColourView', params: { colourId: productCategory.colour.id } }">{{
                  productCategory.colour.hexCode
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="productCategory.product">
                <router-link :to="{ name: 'ProductView', params: { productId: productCategory.product.id } }">{{
                  productCategory.product.name
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ProductCategoryView', params: { productCategoryId: productCategory.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ProductCategoryEdit', params: { productCategoryId: productCategory.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(productCategory)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="foursquareApp.productCategory.delete.question" data-cy="productCategoryDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-productCategory-heading">Are you sure you want to delete Product Category {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-productCategory"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeProductCategory()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./product-category.component.ts"></script>
