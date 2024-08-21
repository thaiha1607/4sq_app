<template>
  <div>
    <h2 id="page-heading" data-cy="ProductQuantityHeading">
      <span id="product-quantity-heading">Product Quantities</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ProductQuantityCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-product-quantity"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Product Quantity</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && productQuantities && productQuantities.length === 0">
      <span>No Product Quantities found</span>
    </div>
    <div class="table-responsive" v-if="productQuantities && productQuantities.length > 0">
      <table class="table table-striped" aria-describedby="productQuantities">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Qty</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Working Unit</span></th>
            <th scope="row"><span>Product Category</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="productQuantity in productQuantities" :key="productQuantity.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ProductQuantityView', params: { productQuantityId: productQuantity.id } }">{{
                productQuantity.id
              }}</router-link>
            </td>
            <td>{{ productQuantity.qty }}</td>
            <td>{{ productQuantity.createdBy }}</td>
            <td>{{ formatDateShort(productQuantity.createdDate) || '' }}</td>
            <td>{{ productQuantity.lastModifiedBy }}</td>
            <td>{{ formatDateShort(productQuantity.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="productQuantity.workingUnit">
                <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: productQuantity.workingUnit.id } }">{{
                  productQuantity.workingUnit.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="productQuantity.productCategory">
                <router-link :to="{ name: 'ProductCategoryView', params: { productCategoryId: productQuantity.productCategory.id } }">{{
                  productQuantity.productCategory.name
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ProductQuantityView', params: { productQuantityId: productQuantity.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ProductQuantityEdit', params: { productQuantityId: productQuantity.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(productQuantity)"
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
        <span id="foursquareApp.productQuantity.delete.question" data-cy="productQuantityDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-productQuantity-heading">Are you sure you want to delete Product Quantity {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-productQuantity"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeProductQuantity()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./product-quantity.component.ts"></script>
