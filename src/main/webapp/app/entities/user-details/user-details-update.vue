<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.userDetails.home.createOrEditLabel" data-cy="UserDetailsCreateUpdateHeading">
          Create or edit a User Details
        </h2>
        <div>
          <div class="form-group" v-if="userDetails.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="userDetails.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-details-phone">Phone</label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="user-details-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
            />
            <div v-if="v$.phone.$anyDirty && v$.phone.$invalid">
              <small class="form-text text-danger" v-for="error of v$.phone.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-details-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="user-details-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-details-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="user-details-createdDate"
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
            <label class="form-control-label" for="user-details-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="user-details-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="user-details-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="user-details-lastModifiedDate"
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
            <label class="form-control-label" for="user-details-user">User</label>
            <select class="form-control" id="user-details-user" data-cy="user" name="user" v-model="userDetails.user" required>
              <option v-if="!userDetails.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="userDetails.user && userOption.id === userDetails.user.id ? userDetails.user : userOption"
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
            <label class="form-control-label" for="user-details-workingUnit">Working Unit</label>
            <select
              class="form-control"
              id="user-details-workingUnit"
              data-cy="workingUnit"
              name="workingUnit"
              v-model="userDetails.workingUnit"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  userDetails.workingUnit && workingUnitOption.id === userDetails.workingUnit.id
                    ? userDetails.workingUnit
                    : workingUnitOption
                "
                v-for="workingUnitOption in workingUnits"
                :key="workingUnitOption.id"
              >
                {{ workingUnitOption.name }}
              </option>
            </select>
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
<script lang="ts" src="./user-details-update.component.ts"></script>
