<template>
  <div>
    <h2 id="page-heading" data-cy="OrderStatusHeading">
      <span id="order-status-heading">Order Statuses</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'OrderStatusCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-order-status"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Order Status</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && orderStatuses && orderStatuses.length === 0">
      <span>No Order Statuses found</span>
    </div>
    <div class="table-responsive" v-if="orderStatuses && orderStatuses.length > 0">
      <table class="table table-striped" aria-describedby="orderStatuses">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Status Code</span></th>
            <th scope="row"><span>Description</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="orderStatus in orderStatuses" :key="orderStatus.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: orderStatus.id } }">{{ orderStatus.id }}</router-link>
            </td>
            <td>{{ orderStatus.statusCode }}</td>
            <td>{{ orderStatus.description }}</td>
            <td>{{ orderStatus.createdBy }}</td>
            <td>{{ formatDateShort(orderStatus.createdDate) || '' }}</td>
            <td>{{ orderStatus.lastModifiedBy }}</td>
            <td>{{ formatDateShort(orderStatus.lastModifiedDate) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: orderStatus.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OrderStatusEdit', params: { orderStatusId: orderStatus.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(orderStatus)"
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
        <span id="foursquareApp.orderStatus.delete.question" data-cy="orderStatusDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-orderStatus-heading">Are you sure you want to delete Order Status {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-orderStatus"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeOrderStatus()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./order-status.component.ts"></script>
