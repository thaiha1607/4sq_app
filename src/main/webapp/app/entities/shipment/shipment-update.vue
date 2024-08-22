<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.shipment.home.createOrEditLabel" data-cy="ShipmentCreateUpdateHeading">Create or edit a Shipment</h2>
        <div>
          <div class="form-group" v-if="shipment.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shipment.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="shipment-type"
              data-cy="type"
              required
            >
              <option v-for="shipmentType in shipmentTypeValues" :key="shipmentType" v-bind:value="shipmentType">{{ shipmentType }}</option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-shipmentDate">Shipment Date</label>
            <div class="d-flex">
              <input
                id="shipment-shipmentDate"
                data-cy="shipmentDate"
                type="datetime-local"
                class="form-control"
                name="shipmentDate"
                :class="{ valid: !v$.shipmentDate.$invalid, invalid: v$.shipmentDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.shipmentDate.$model)"
                @change="updateInstantField('shipmentDate', $event)"
              />
            </div>
            <div v-if="v$.shipmentDate.$anyDirty && v$.shipmentDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.shipmentDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="shipment-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="shipment-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="shipment-createdDate"
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
            <label class="form-control-label" for="shipment-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="shipment-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="shipment-lastModifiedDate"
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
          <div v-if="v$.items.$anyDirty && v$.items.$invalid">
            <small class="form-text text-danger" v-for="error of v$.items.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-status">Status</label>
            <select class="form-control" id="shipment-status" data-cy="status" name="status" v-model="shipment.status" required>
              <option v-if="!shipment.status" v-bind:value="null" selected></option>
              <option
                v-bind:value="shipment.status && shipmentStatusOption.id === shipment.status.id ? shipment.status : shipmentStatusOption"
                v-for="shipmentStatusOption in shipmentStatuses"
                :key="shipmentStatusOption.id"
              >
                {{ shipmentStatusOption.statusCode }}
              </option>
            </select>
          </div>
          <div v-if="v$.status.$anyDirty && v$.status.$invalid">
            <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-order">Order</label>
            <select class="form-control" id="shipment-order" data-cy="order" name="order" v-model="shipment.order" required>
              <option v-if="!shipment.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="shipment.order && orderOption.id === shipment.order.id ? shipment.order : orderOption"
                v-for="orderOption in orders"
                :key="orderOption.id"
              >
                {{ orderOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.order.$anyDirty && v$.order.$invalid">
            <small class="form-text text-danger" v-for="error of v$.order.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-invoice">Invoice</label>
            <select class="form-control" id="shipment-invoice" data-cy="invoice" name="invoice" v-model="shipment.invoice" required>
              <option v-if="!shipment.invoice" v-bind:value="null" selected></option>
              <option
                v-bind:value="shipment.invoice && invoiceOption.id === shipment.invoice.id ? shipment.invoice : invoiceOption"
                v-for="invoiceOption in invoices"
                :key="invoiceOption.id"
              >
                {{ invoiceOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.invoice.$anyDirty && v$.invoice.$invalid">
            <small class="form-text text-danger" v-for="error of v$.invoice.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./shipment-update.component.ts"></script>
