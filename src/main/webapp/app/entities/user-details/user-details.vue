<template>
  <div>
    <h2 id="page-heading" data-cy="UserDetailsHeading">
      <span id="user-details-heading">User Details</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'UserDetailsCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-user-details"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new User Details</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && userDetails && userDetails.length === 0">
      <span>No User Details found</span>
    </div>
    <div class="table-responsive" v-if="userDetails && userDetails.length > 0">
      <table class="table table-striped" aria-describedby="userDetails">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Phone</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>User</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="userDetails in userDetails" :key="userDetails.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'UserDetailsView', params: { userDetailsId: userDetails.id } }">{{ userDetails.id }}</router-link>
            </td>
            <td>{{ userDetails.phone }}</td>
            <td>{{ userDetails.createdBy }}</td>
            <td>{{ formatDateShort(userDetails.createdDate) || '' }}</td>
            <td>{{ userDetails.lastModifiedBy }}</td>
            <td>{{ formatDateShort(userDetails.lastModifiedDate) || '' }}</td>
            <td>
              {{ userDetails.user ? userDetails.user.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'UserDetailsView', params: { userDetailsId: userDetails.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'UserDetailsEdit', params: { userDetailsId: userDetails.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(userDetails)"
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
        <span id="foursquareApp.userDetails.delete.question" data-cy="userDetailsDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-userDetails-heading">Are you sure you want to delete User Details {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-userDetails"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeUserDetails()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./user-details.component.ts"></script>
