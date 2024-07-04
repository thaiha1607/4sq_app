<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.address.home.createOrEditLabel" data-cy="AddressCreateUpdateHeading">Create or edit a Address</h2>
        <div>
          <div class="form-group" v-if="address.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="address.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-line1">Line 1</label>
            <input
              type="text"
              class="form-control"
              name="line1"
              id="address-line1"
              data-cy="line1"
              :class="{ valid: !v$.line1.$invalid, invalid: v$.line1.$invalid }"
              v-model="v$.line1.$model"
              required
            />
            <div v-if="v$.line1.$anyDirty && v$.line1.$invalid">
              <small class="form-text text-danger" v-for="error of v$.line1.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-line2">Line 2</label>
            <input
              type="text"
              class="form-control"
              name="line2"
              id="address-line2"
              data-cy="line2"
              :class="{ valid: !v$.line2.$invalid, invalid: v$.line2.$invalid }"
              v-model="v$.line2.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-city">City</label>
            <input
              type="text"
              class="form-control"
              name="city"
              id="address-city"
              data-cy="city"
              :class="{ valid: !v$.city.$invalid, invalid: v$.city.$invalid }"
              v-model="v$.city.$model"
              required
            />
            <div v-if="v$.city.$anyDirty && v$.city.$invalid">
              <small class="form-text text-danger" v-for="error of v$.city.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-state">State</label>
            <input
              type="text"
              class="form-control"
              name="state"
              id="address-state"
              data-cy="state"
              :class="{ valid: !v$.state.$invalid, invalid: v$.state.$invalid }"
              v-model="v$.state.$model"
              required
            />
            <div v-if="v$.state.$anyDirty && v$.state.$invalid">
              <small class="form-text text-danger" v-for="error of v$.state.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-country">Country</label>
            <input
              type="text"
              class="form-control"
              name="country"
              id="address-country"
              data-cy="country"
              :class="{ valid: !v$.country.$invalid, invalid: v$.country.$invalid }"
              v-model="v$.country.$model"
              required
            />
            <div v-if="v$.country.$anyDirty && v$.country.$invalid">
              <small class="form-text text-danger" v-for="error of v$.country.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-zipOrPostalCode">Zip Or Postal Code</label>
            <input
              type="text"
              class="form-control"
              name="zipOrPostalCode"
              id="address-zipOrPostalCode"
              data-cy="zipOrPostalCode"
              :class="{ valid: !v$.zipOrPostalCode.$invalid, invalid: v$.zipOrPostalCode.$invalid }"
              v-model="v$.zipOrPostalCode.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="address-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="address-createdDate"
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
            <label class="form-control-label" for="address-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="address-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="address-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="address-lastModifiedDate"
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
<script lang="ts" src="./address-update.component.ts"></script>
