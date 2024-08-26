<template>
  <div>
    <h2 id="page-heading" data-cy="OrderHeading">
      <span id="order-heading">Orders</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'OrderCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-order"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Order</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && orders && orders.length === 0">
      <span>No Orders found</span>
    </div>
    <div class="table-responsive" v-if="orders && orders.length > 0">
      <table class="table table-striped" aria-describedby="orders">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Type</span></th>
            <th scope="row"><span>Priority</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Other Info</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Customer</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Address</span></th>
            <th scope="row"><span>Root Order</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OrderView', params: { orderId: order.id } }">{{ order.id }}</router-link>
            </td>
            <td>{{ order.type }}</td>
            <td>{{ order.priority }}</td>
            <td>{{ order.note }}</td>
            <td>{{ order.otherInfo }}</td>
            <td>{{ order.createdBy }}</td>
            <td>{{ formatDateShort(order.createdDate) || '' }}</td>
            <td>{{ order.lastModifiedBy }}</td>
            <td>{{ formatDateShort(order.lastModifiedDate) || '' }}</td>
            <td>
              {{ order.customer ? order.customer.login : '' }}
            </td>
            <td>
              <div v-if="order.status">
                <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: order.status.id } }">{{
                  order.status.statusCode
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="order.address">
                <router-link :to="{ name: 'AddressView', params: { addressId: order.address.id } }">{{ order.address.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="order.rootOrder">
                <router-link :to="{ name: 'OrderView', params: { orderId: order.rootOrder.id } }">{{ order.rootOrder.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OrderView', params: { orderId: order.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OrderEdit', params: { orderId: order.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(order)"
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
        <span id="foursquareApp.order.delete.question" data-cy="orderDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-order-heading">Are you sure you want to delete Order {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-order"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeOrder()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./order.component.ts"></script>
