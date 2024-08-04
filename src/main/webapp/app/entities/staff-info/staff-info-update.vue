<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.staffInfo.home.createOrEditLabel" data-cy="StaffInfoCreateUpdateHeading">Create or edit a Staff Info</h2>
        <div>
          <div class="form-group" v-if="staffInfo.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="staffInfo.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="staff-info-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="staff-info-status"
              data-cy="status"
              required
            >
              <option v-for="staffStatus in staffStatusValues" :key="staffStatus" v-bind:value="staffStatus">{{ staffStatus }}</option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="staff-info-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="staff-info-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="staff-info-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="staff-info-createdDate"
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
            <label class="form-control-label" for="staff-info-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="staff-info-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="staff-info-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="staff-info-lastModifiedDate"
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
            <label class="form-control-label" for="staff-info-user">User</label>
            <select class="form-control" id="staff-info-user" data-cy="user" name="user" v-model="staffInfo.user" required>
              <option v-if="!staffInfo.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="staffInfo.user && userOption.id === staffInfo.user.id ? staffInfo.user : userOption"
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
            <label class="form-control-label" for="staff-info-workingUnit">Working Unit</label>
            <select
              class="form-control"
              id="staff-info-workingUnit"
              data-cy="workingUnit"
              name="workingUnit"
              v-model="staffInfo.workingUnit"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  staffInfo.workingUnit && workingUnitOption.id === staffInfo.workingUnit.id ? staffInfo.workingUnit : workingUnitOption
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
<script lang="ts" src="./staff-info-update.component.ts"></script>
