<template>
  <div>
    <h2 id="page-heading" data-cy="WorkingUnitHeading">
      <span id="working-unit-heading">Working Units</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'WorkingUnitCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-working-unit"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Working Unit</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && workingUnits && workingUnits.length === 0">
      <span>No Working Units found</span>
    </div>
    <div class="table-responsive" v-if="workingUnits && workingUnits.length > 0">
      <table class="table table-striped" aria-describedby="workingUnits">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Type</span></th>
            <th scope="row"><span>Image Uri</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>Address</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="workingUnit in workingUnits" :key="workingUnit.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: workingUnit.id } }">{{ workingUnit.id }}</router-link>
            </td>
            <td>{{ workingUnit.name }}</td>
            <td>{{ workingUnit.type }}</td>
            <td>{{ workingUnit.imageUri }}</td>
            <td>{{ workingUnit.createdBy }}</td>
            <td>{{ formatDateShort(workingUnit.createdDate) || '' }}</td>
            <td>{{ workingUnit.lastModifiedBy }}</td>
            <td>{{ formatDateShort(workingUnit.lastModifiedDate) || '' }}</td>
            <td>
              <div v-if="workingUnit.address">
                <router-link :to="{ name: 'AddressView', params: { addressId: workingUnit.address.id } }">{{
                  workingUnit.address.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: workingUnit.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'WorkingUnitEdit', params: { workingUnitId: workingUnit.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(workingUnit)"
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
        <span id="foursquareApp.workingUnit.delete.question" data-cy="workingUnitDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-workingUnit-heading">Are you sure you want to delete Working Unit {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-workingUnit"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeWorkingUnit()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./working-unit.component.ts"></script>
