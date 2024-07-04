<template>
  <div>
    <h2 id="page-heading" data-cy="UserAddressHeading">
      <span id="user-address-heading">User Addresses</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'UserAddressCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-user-address"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new User Address</span>
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
              placeholder="Search for User Address"
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
    <div class="alert alert-warning" v-if="!isFetching && userAddresses && userAddresses.length === 0">
      <span>No User Addresses found</span>
    </div>
    <div class="table-responsive" v-if="userAddresses && userAddresses.length > 0">
      <table class="table table-striped" aria-describedby="userAddresses">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Type</span></th>
            <th scope="row"><span>Friendly Name</span></th>
            <th scope="row"><span>Is Default</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"><span>User</span></th>
            <th scope="row"><span>Address</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="userAddress in userAddresses" :key="userAddress.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'UserAddressView', params: { userAddressId: userAddress.id } }">{{ userAddress.id }}</router-link>
            </td>
            <td>{{ userAddress.type }}</td>
            <td>{{ userAddress.friendlyName }}</td>
            <td>{{ userAddress.isDefault }}</td>
            <td>{{ userAddress.createdBy }}</td>
            <td>{{ formatDateShort(userAddress.createdDate) || '' }}</td>
            <td>{{ userAddress.lastModifiedBy }}</td>
            <td>{{ formatDateShort(userAddress.lastModifiedDate) || '' }}</td>
            <td>
              {{ userAddress.user ? userAddress.user.login : '' }}
            </td>
            <td>
              <div v-if="userAddress.address">
                <router-link :to="{ name: 'AddressView', params: { addressId: userAddress.address.id } }">{{
                  userAddress.address.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'UserAddressView', params: { userAddressId: userAddress.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'UserAddressEdit', params: { userAddressId: userAddress.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(userAddress)"
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
        <span id="foursquareApp.userAddress.delete.question" data-cy="userAddressDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-userAddress-heading">Are you sure you want to delete User Address {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-userAddress"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeUserAddress()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./user-address.component.ts"></script>
