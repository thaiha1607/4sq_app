<template>
  <div>
    <h2 id="page-heading" data-cy="ShipmentHeading">
      <span id="shipment-heading">Shipments</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ShipmentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-shipment"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Shipment</span>
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
              placeholder="Search for Shipment"
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
    <div class="alert alert-warning" v-if="!isFetching && shipments && shipments.length === 0">
      <span>No Shipments found</span>
    </div>
    <div class="table-responsive" v-if="shipments && shipments.length > 0">
      <table class="table table-striped" aria-describedby="shipments">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Type</span></th>
            <th scope="row"><span>Shipment Date</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Order</span></th>
            <th scope="row"><span>Invoice</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="shipment in shipments" :key="shipment.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipment.id } }">{{ shipment.id }}</router-link>
            </td>
            <td>{{ shipment.type }}</td>
            <td>{{ formatDateShort(shipment.shipmentDate) || '' }}</td>
            <td>{{ shipment.note }}</td>
            <td>{{ shipment.createdBy }}</td>
            <td>{{ formatDateShort(shipment.createdDate) || '' }}</td>
            <td>{{ shipment.lastModifiedBy }}</td>
            <td>{{ formatDateShort(shipment.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="shipment.status">
                <router-link :to="{ name: 'ShipmentStatusView', params: { shipmentStatusId: shipment.status.id } }">{{
                  shipment.status.statusCode
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="shipment.order">
                <router-link :to="{ name: 'OrderView', params: { orderId: shipment.order.id } }">{{ shipment.order.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="shipment.invoice">
                <router-link :to="{ name: 'InvoiceView', params: { invoiceId: shipment.invoice.id } }">{{
                  shipment.invoice.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ShipmentEdit', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(shipment)"
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
        <span id="foursquareApp.shipment.delete.question" data-cy="shipmentDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shipment-heading">Are you sure you want to delete Shipment {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shipment"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeShipment()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./shipment.component.ts"></script>
