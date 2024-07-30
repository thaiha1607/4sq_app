<template>
  <div>
    <h2 id="page-heading" data-cy="ShipmentStatusHeading">
      <span id="shipment-status-heading">Shipment Statuses</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ShipmentStatusCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-shipment-status"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Shipment Status</span>
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
              placeholder="Search for Shipment Status"
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
    <div class="alert alert-warning" v-if="!isFetching && shipmentStatuses && shipmentStatuses.length === 0">
      <span>No Shipment Statuses found</span>
    </div>
    <div class="table-responsive" v-if="shipmentStatuses && shipmentStatuses.length > 0">
      <table class="table table-striped" aria-describedby="shipmentStatuses">
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
          <tr v-for="shipmentStatus in shipmentStatuses" :key="shipmentStatus.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShipmentStatusView', params: { shipmentStatusId: shipmentStatus.id } }">{{
                shipmentStatus.id
              }}</router-link>
            </td>
            <td>{{ shipmentStatus.statusCode }}</td>
            <td>{{ shipmentStatus.description }}</td>
            <td>{{ shipmentStatus.createdBy }}</td>
            <td>{{ formatDateShort(shipmentStatus.createdDate) || '' }}</td>
            <td>{{ shipmentStatus.lastModifiedBy }}</td>
            <td>{{ formatDateShort(shipmentStatus.lastModifiedDate) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ShipmentStatusView', params: { shipmentStatusId: shipmentStatus.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ShipmentStatusEdit', params: { shipmentStatusId: shipmentStatus.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(shipmentStatus)"
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
        <span id="foursquareApp.shipmentStatus.delete.question" data-cy="shipmentStatusDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shipmentStatus-heading">Are you sure you want to delete Shipment Status {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shipmentStatus"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeShipmentStatus()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./shipment-status.component.ts"></script>
