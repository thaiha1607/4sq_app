<template>
  <div>
    <h2 id="page-heading" data-cy="WarehouseAssignmentHeading">
      <span id="warehouse-assignment-heading">Warehouse Assignments</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'WarehouseAssignmentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-warehouse-assignment"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Warehouse Assignment</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && warehouseAssignments && warehouseAssignments.length === 0">
      <span>No Warehouse Assignments found</span>
    </div>
    <div class="table-responsive" v-if="warehouseAssignments && warehouseAssignments.length > 0">
      <table class="table table-striped" aria-describedby="warehouseAssignments">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Other Info</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>User</span></th>
            <th scope="row"><span>Source Working Unit</span></th>
            <th scope="row"><span>Target Working Unit</span></th>
            <th scope="row"><span>Internal Order</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="warehouseAssignment in warehouseAssignments" :key="warehouseAssignment.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'WarehouseAssignmentView', params: { warehouseAssignmentId: warehouseAssignment.id } }">{{
                warehouseAssignment.id
              }}</router-link>
            </td>
            <td>{{ warehouseAssignment.status }}</td>
            <td>{{ warehouseAssignment.note }}</td>
            <td>{{ warehouseAssignment.otherInfo }}</td>
            <td>{{ warehouseAssignment.createdBy }}</td>
            <td>{{ formatDateShort(warehouseAssignment.createdDate) || '' }}</td>
            <td>{{ warehouseAssignment.lastModifiedBy }}</td>
            <td>{{ formatDateShort(warehouseAssignment.lastModifiedDate) || '' }}</td>
            <td>
              {{ warehouseAssignment.user ? warehouseAssignment.user.login : '' }}
            </td>
            <td>
              <div v-if="warehouseAssignment.sourceWorkingUnit">
                <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: warehouseAssignment.sourceWorkingUnit.id } }">{{
                  warehouseAssignment.sourceWorkingUnit.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="warehouseAssignment.targetWorkingUnit">
                <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: warehouseAssignment.targetWorkingUnit.id } }">{{
                  warehouseAssignment.targetWorkingUnit.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="warehouseAssignment.internalOrder">
                <router-link :to="{ name: 'InternalOrderView', params: { internalOrderId: warehouseAssignment.internalOrder.id } }">{{
                  warehouseAssignment.internalOrder.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'WarehouseAssignmentView', params: { warehouseAssignmentId: warehouseAssignment.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'WarehouseAssignmentEdit', params: { warehouseAssignmentId: warehouseAssignment.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(warehouseAssignment)"
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
        <span id="foursquareApp.warehouseAssignment.delete.question" data-cy="warehouseAssignmentDeleteDialogHeading"
          >Confirm delete operation</span
        >
      </template>
      <div class="modal-body">
        <p id="jhi-delete-warehouseAssignment-heading">Are you sure you want to delete Warehouse Assignment {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-warehouseAssignment"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeWarehouseAssignment()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./warehouse-assignment.component.ts"></script>
