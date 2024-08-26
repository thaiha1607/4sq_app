<template>
  <div>
    <h2 id="page-heading" data-cy="InternalOrderHistoryHeading">
      <span id="internal-order-history-heading">Internal Order Histories</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'InternalOrderHistoryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-internal-order-history"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Internal Order History</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && internalOrderHistories && internalOrderHistories.length === 0">
      <span>No Internal Order Histories found</span>
    </div>
    <div class="table-responsive" v-if="internalOrderHistories && internalOrderHistories.length > 0">
      <table class="table table-striped" aria-describedby="internalOrderHistories">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Order</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="internalOrderHistory in internalOrderHistories" :key="internalOrderHistory.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InternalOrderHistoryView', params: { internalOrderHistoryId: internalOrderHistory.id } }">{{
                internalOrderHistory.id
              }}</router-link>
            </td>
            <td>{{ internalOrderHistory.note }}</td>
            <td>{{ internalOrderHistory.createdBy }}</td>
            <td>{{ formatDateShort(internalOrderHistory.createdDate) || '' }}</td>
            <td>{{ internalOrderHistory.lastModifiedBy }}</td>
            <td>{{ formatDateShort(internalOrderHistory.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="internalOrderHistory.status">
                <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: internalOrderHistory.status.id } }">{{
                  internalOrderHistory.status.statusCode
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="internalOrderHistory.order">
                <router-link :to="{ name: 'InternalOrderView', params: { internalOrderId: internalOrderHistory.order.id } }">{{
                  internalOrderHistory.order.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'InternalOrderHistoryView', params: { internalOrderHistoryId: internalOrderHistory.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'InternalOrderHistoryEdit', params: { internalOrderHistoryId: internalOrderHistory.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(internalOrderHistory)"
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
        <span id="foursquareApp.internalOrderHistory.delete.question" data-cy="internalOrderHistoryDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-internalOrderHistory-heading">Are you sure you want to delete Internal Order History {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-internalOrderHistory"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeInternalOrderHistory()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./internal-order-history.component.ts"></script>
