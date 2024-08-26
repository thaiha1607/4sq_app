<template>
  <div>
    <h2 id="page-heading" data-cy="InternalOrderItemHeading">
      <span id="internal-order-item-heading">Internal Order Items</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'InternalOrderItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-internal-order-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Internal Order Item</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && internalOrderItems && internalOrderItems.length === 0">
      <span>No Internal Order Items found</span>
    </div>
    <div class="table-responsive" v-if="internalOrderItems && internalOrderItems.length > 0">
      <table class="table table-striped" aria-describedby="internalOrderItems">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Qty</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Order Item</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="internalOrderItem in internalOrderItems" :key="internalOrderItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InternalOrderItemView', params: { internalOrderItemId: internalOrderItem.id } }">{{
                internalOrderItem.id
              }}</router-link>
            </td>
            <td>{{ internalOrderItem.qty }}</td>
            <td>{{ internalOrderItem.note }}</td>
            <td>{{ internalOrderItem.createdBy }}</td>
            <td>{{ formatDateShort(internalOrderItem.createdDate) || '' }}</td>
            <td>{{ internalOrderItem.lastModifiedBy }}</td>
            <td>{{ formatDateShort(internalOrderItem.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="internalOrderItem.orderItem">
                <router-link :to="{ name: 'OrderItemView', params: { orderItemId: internalOrderItem.orderItem.id } }">{{
                  internalOrderItem.orderItem.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'InternalOrderItemView', params: { internalOrderItemId: internalOrderItem.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'InternalOrderItemEdit', params: { internalOrderItemId: internalOrderItem.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(internalOrderItem)"
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
        <span id="foursquareApp.internalOrderItem.delete.question" data-cy="internalOrderItemDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-internalOrderItem-heading">Are you sure you want to delete Internal Order Item {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-internalOrderItem"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeInternalOrderItem()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./internal-order-item.component.ts"></script>
