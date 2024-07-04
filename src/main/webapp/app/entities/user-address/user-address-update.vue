<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.userAddress.home.createOrEditLabel" data-cy="UserAddressCreateUpdateHeading">
          Create or edit a User Address
        </h2>
        <div>
          <div class="form-group" v-if="userAddress.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="userAddress.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="user-address-type"
              data-cy="type"
              required
            >
              <option v-for="addressType in addressTypeValues" :key="addressType" v-bind:value="addressType">{{ addressType }}</option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-friendlyName">Friendly Name</label>
            <input
              type="text"
              class="form-control"
              name="friendlyName"
              id="user-address-friendlyName"
              data-cy="friendlyName"
              :class="{ valid: !v$.friendlyName.$invalid, invalid: v$.friendlyName.$invalid }"
              v-model="v$.friendlyName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-isDefault">Is Default</label>
            <input
              type="checkbox"
              class="form-check"
              name="isDefault"
              id="user-address-isDefault"
              data-cy="isDefault"
              :class="{ valid: !v$.isDefault.$invalid, invalid: v$.isDefault.$invalid }"
              v-model="v$.isDefault.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="user-address-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="user-address-createdDate"
                data-cy="createdDate"
                type="datetime-local"
                class="form-control"
                name="createdDate"
                :class="{ valid: !v$.createdDate.$invalid, invalid: v$.createdDate.$invalid }"
                :value="convertDateTimeFromServer(v$.createdDate.$model)"
                @change="updateInstantField('createdDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="user-address-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="user-address-lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                class="form-control"
                name="lastModifiedDate"
                :class="{ valid: !v$.lastModifiedDate.$invalid, invalid: v$.lastModifiedDate.$invalid }"
                :value="convertDateTimeFromServer(v$.lastModifiedDate.$model)"
                @change="updateInstantField('lastModifiedDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-user">User</label>
            <select class="form-control" id="user-address-user" data-cy="user" name="user" v-model="userAddress.user" required>
              <option v-if="!userAddress.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="userAddress.user && userOption.id === userAddress.user.id ? userAddress.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="v$.user.$anyDirty && v$.user.$invalid">
            <small class="form-text text-danger" v-for="error of v$.user.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-address-address">Address</label>
            <select class="form-control" id="user-address-address" data-cy="address" name="address" v-model="userAddress.address" required>
              <option v-if="!userAddress.address" v-bind:value="null" selected></option>
              <option
                v-bind:value="userAddress.address && addressOption.id === userAddress.address.id ? userAddress.address : addressOption"
                v-for="addressOption in addresses"
                :key="addressOption.id"
              >
                {{ addressOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.address.$anyDirty && v$.address.$invalid">
            <small class="form-text text-danger" v-for="error of v$.address.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./user-address-update.component.ts"></script>
