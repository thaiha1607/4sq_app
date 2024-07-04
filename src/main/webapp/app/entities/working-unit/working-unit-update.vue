<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.workingUnit.home.createOrEditLabel" data-cy="WorkingUnitCreateUpdateHeading">
          Create or edit a Working Unit
        </h2>
        <div>
          <div class="form-group" v-if="workingUnit.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="workingUnit.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="working-unit-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="working-unit-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="working-unit-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="working-unit-type"
              data-cy="type"
              required
            >
              <option v-for="workingUnitType in workingUnitTypeValues" :key="workingUnitType" v-bind:value="workingUnitType">
                {{ workingUnitType }}
              </option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="working-unit-imageUri">Image Uri</label>
            <input
              type="text"
              class="form-control"
              name="imageUri"
              id="working-unit-imageUri"
              data-cy="imageUri"
              :class="{ valid: !v$.imageUri.$invalid, invalid: v$.imageUri.$invalid }"
              v-model="v$.imageUri.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="working-unit-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="working-unit-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="working-unit-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="working-unit-createdDate"
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
            <label class="form-control-label" for="working-unit-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="working-unit-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="working-unit-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="working-unit-lastModifiedDate"
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
            <label class="form-control-label" for="working-unit-address">Address</label>
            <select class="form-control" id="working-unit-address" data-cy="address" name="address" v-model="workingUnit.address">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="workingUnit.address && addressOption.id === workingUnit.address.id ? workingUnit.address : addressOption"
                v-for="addressOption in addresses"
                :key="addressOption.id"
              >
                {{ addressOption.id }}
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
<script lang="ts" src="./working-unit-update.component.ts"></script>
