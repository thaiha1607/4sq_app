<template>
  <div>
    <h2 id="page-heading" data-cy="InvoiceStatusHeading">
      <span id="invoice-status-heading">Invoice Statuses</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'InvoiceStatusCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-invoice-status"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Invoice Status</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && invoiceStatuses && invoiceStatuses.length === 0">
      <span>No Invoice Statuses found</span>
    </div>
    <div class="table-responsive" v-if="invoiceStatuses && invoiceStatuses.length > 0">
      <table class="table table-striped" aria-describedby="invoiceStatuses">
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
          <tr v-for="invoiceStatus in invoiceStatuses" :key="invoiceStatus.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InvoiceStatusView', params: { invoiceStatusId: invoiceStatus.id } }">{{
                invoiceStatus.id
              }}</router-link>
            </td>
            <td>{{ invoiceStatus.statusCode }}</td>
            <td>{{ invoiceStatus.description }}</td>
            <td>{{ invoiceStatus.createdBy }}</td>
            <td>{{ formatDateShort(invoiceStatus.createdDate) || '' }}</td>
            <td>{{ invoiceStatus.lastModifiedBy }}</td>
            <td>{{ formatDateShort(invoiceStatus.lastModifiedDate) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'InvoiceStatusView', params: { invoiceStatusId: invoiceStatus.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'InvoiceStatusEdit', params: { invoiceStatusId: invoiceStatus.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(invoiceStatus)"
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
        <span id="foursquareApp.invoiceStatus.delete.question" data-cy="invoiceStatusDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-invoiceStatus-heading">Are you sure you want to delete Invoice Status {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-invoiceStatus"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeInvoiceStatus()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./invoice-status.component.ts"></script>
