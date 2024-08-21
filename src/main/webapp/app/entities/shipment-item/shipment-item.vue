<template>
  <div>
    <h2 id="page-heading" data-cy="ShipmentItemHeading">
      <span id="shipment-item-heading">Shipment Items</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ShipmentItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-shipment-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Shipment Item</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && shipmentItems && shipmentItems.length === 0">
      <span>No Shipment Items found</span>
    </div>
    <div class="table-responsive" v-if="shipmentItems && shipmentItems.length > 0">
      <table class="table table-striped" aria-describedby="shipmentItems">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Qty</span></th>
            <th scope="row"><span>Total</span></th>
            <th scope="row"><span>Roll Qty</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Order Item</span></th>
            <th scope="row"><span>Shipment</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="shipmentItem in shipmentItems" :key="shipmentItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShipmentItemView', params: { shipmentItemId: shipmentItem.id } }">{{
                shipmentItem.id
              }}</router-link>
            </td>
            <td>{{ shipmentItem.qty }}</td>
            <td>{{ shipmentItem.total }}</td>
            <td>{{ shipmentItem.rollQty }}</td>
            <td>{{ shipmentItem.createdBy }}</td>
            <td>{{ formatDateShort(shipmentItem.createdDate) || '' }}</td>
            <td>{{ shipmentItem.lastModifiedBy }}</td>
            <td>{{ formatDateShort(shipmentItem.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="shipmentItem.orderItem">
                <router-link :to="{ name: 'OrderItemView', params: { orderItemId: shipmentItem.orderItem.id } }">{{
                  shipmentItem.orderItem.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="shipmentItem.shipment">
                <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipmentItem.shipment.id } }">{{
                  shipmentItem.shipment.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ShipmentItemView', params: { shipmentItemId: shipmentItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ShipmentItemEdit', params: { shipmentItemId: shipmentItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(shipmentItem)"
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
        <span id="foursquareApp.shipmentItem.delete.question" data-cy="shipmentItemDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shipmentItem-heading">Are you sure you want to delete Shipment Item {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shipmentItem"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeShipmentItem()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./shipment-item.component.ts"></script>
