<template>
  <div>
    <h2 id="page-heading" data-cy="StaffInfoHeading">
      <span id="staff-info-heading">Staff Infos</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'StaffInfoCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-staff-info"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Staff Info</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && staffInfos && staffInfos.length === 0">
      <span>No Staff Infos found</span>
    </div>
    <div class="table-responsive" v-if="staffInfos && staffInfos.length > 0">
      <table class="table table-striped" aria-describedby="staffInfos">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>User</span></th>
            <th scope="row"><span>Working Unit</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="staffInfo in staffInfos" :key="staffInfo.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'StaffInfoView', params: { staffInfoId: staffInfo.id } }">{{ staffInfo.id }}</router-link>
            </td>
            <td>{{ staffInfo.status }}</td>
            <td>{{ staffInfo.createdBy }}</td>
            <td>{{ formatDateShort(staffInfo.createdDate) || '' }}</td>
            <td>{{ staffInfo.lastModifiedBy }}</td>
            <td>{{ formatDateShort(staffInfo.lastModifiedDate) || '' }}</td>
            <td>
              {{ staffInfo.user ? staffInfo.user.login : '' }}
            </td>
            <td>
              <div v-if="staffInfo.workingUnit">
                <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: staffInfo.workingUnit.id } }">{{
                  staffInfo.workingUnit.name
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'StaffInfoView', params: { staffInfoId: staffInfo.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'StaffInfoEdit', params: { staffInfoId: staffInfo.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(staffInfo)"
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
        <span id="foursquareApp.staffInfo.delete.question" data-cy="staffInfoDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-staffInfo-heading">Are you sure you want to delete Staff Info {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-staffInfo"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeStaffInfo()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./staff-info.component.ts"></script>
