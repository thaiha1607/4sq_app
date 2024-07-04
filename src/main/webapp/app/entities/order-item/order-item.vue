<template>
  <div>
    <h2 id="page-heading" data-cy="OrderItemHeading">
      <span id="order-item-heading">Order Items</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'OrderItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-order-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Order Item</span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              placeholder="Search for Order Item"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && orderItems && orderItems.length === 0">
      <span>No Order Items found</span>
    </div>
    <div class="table-responsive" v-if="orderItems && orderItems.length > 0">
      <table class="table table-striped" aria-describedby="orderItems">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Ordered Qty</span></th>
            <th scope="row"><span>Received Qty</span></th>
            <th scope="row"><span>Unit Price</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Product Category</span></th>
            <th scope="row"><span>Order</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="orderItem in orderItems" :key="orderItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OrderItemView', params: { orderItemId: orderItem.id } }">{{ orderItem.id }}</router-link>
            </td>
            <td>{{ orderItem.orderedQty }}</td>
            <td>{{ orderItem.receivedQty }}</td>
            <td>{{ orderItem.unitPrice }}</td>
            <td>{{ orderItem.note }}</td>
            <td>{{ orderItem.createdBy }}</td>
            <td>{{ formatDateShort(orderItem.createdDate) || '' }}</td>
            <td>{{ orderItem.lastModifiedBy }}</td>
            <td>{{ formatDateShort(orderItem.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="orderItem.productCategory">
                <router-link :to="{ name: 'ProductCategoryView', params: { productCategoryId: orderItem.productCategory.id } }">{{
                  orderItem.productCategory.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="orderItem.order">
                <router-link :to="{ name: 'OrderView', params: { orderId: orderItem.order.id } }">{{ orderItem.order.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OrderItemView', params: { orderItemId: orderItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OrderItemEdit', params: { orderItemId: orderItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(orderItem)"
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
        <span id="foursquareApp.orderItem.delete.question" data-cy="orderItemDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-orderItem-heading">Are you sure you want to delete Order Item {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-orderItem"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeOrderItem()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./order-item.component.ts"></script>
