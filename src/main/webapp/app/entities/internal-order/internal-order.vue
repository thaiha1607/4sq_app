<template>
  <div>
    <h2 id="page-heading" data-cy="InternalOrderHeading">
      <span id="internal-order-heading">Internal Orders</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'InternalOrderCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-internal-order"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Internal Order</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && internalOrders && internalOrders.length === 0">
      <span>No Internal Orders found</span>
    </div>
    <div class="table-responsive" v-if="internalOrders && internalOrders.length > 0">
      <table class="table table-striped" aria-describedby="internalOrders">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Type</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Root Order</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="internalOrder in internalOrders" :key="internalOrder.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InternalOrderView', params: { internalOrderId: internalOrder.id } }">{{
                internalOrder.id
              }}</router-link>
            </td>
            <td>{{ internalOrder.type }}</td>
            <td>{{ internalOrder.note }}</td>
            <td>{{ internalOrder.createdBy }}</td>
            <td>{{ formatDateShort(internalOrder.createdDate) || '' }}</td>
            <td>{{ internalOrder.lastModifiedBy }}</td>
            <td>{{ formatDateShort(internalOrder.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="internalOrder.status">
                <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: internalOrder.status.id } }">{{
                  internalOrder.status.statusCode
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="internalOrder.rootOrder">
                <router-link :to="{ name: 'OrderView', params: { orderId: internalOrder.rootOrder.id } }">{{
                  internalOrder.rootOrder.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'InternalOrderView', params: { internalOrderId: internalOrder.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'InternalOrderEdit', params: { internalOrderId: internalOrder.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(internalOrder)"
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
        <span id="foursquareApp.internalOrder.delete.question" data-cy="internalOrderDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-internalOrder-heading">Are you sure you want to delete Internal Order {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-internalOrder"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeInternalOrder()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./internal-order.component.ts"></script>
