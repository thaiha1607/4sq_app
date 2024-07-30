<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.invoiceStatus.home.createOrEditLabel" data-cy="InvoiceStatusCreateUpdateHeading">
          Create or edit a Invoice Status
        </h2>
        <div>
          <div class="form-group" v-if="invoiceStatus.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="invoiceStatus.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-status-statusCode">Status Code</label>
            <input
              type="text"
              class="form-control"
              name="statusCode"
              id="invoice-status-statusCode"
              data-cy="statusCode"
              :class="{ valid: !v$.statusCode.$invalid, invalid: v$.statusCode.$invalid }"
              v-model="v$.statusCode.$model"
              required
            />
            <div v-if="v$.statusCode.$anyDirty && v$.statusCode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.statusCode.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-status-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="invoice-status-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-status-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="invoice-status-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-status-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="invoice-status-createdDate"
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
            <label class="form-control-label" for="invoice-status-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="invoice-status-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-status-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="invoice-status-lastModifiedDate"
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
<script lang="ts" src="./invoice-status-update.component.ts"></script>
