<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.invoice.home.createOrEditLabel" data-cy="InvoiceCreateUpdateHeading">Create or edit a Invoice</h2>
        <div>
          <div class="form-group" v-if="invoice.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="invoice.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-totalAmount">Total Amount</label>
            <input
              type="number"
              class="form-control"
              name="totalAmount"
              id="invoice-totalAmount"
              data-cy="totalAmount"
              :class="{ valid: !v$.totalAmount.$invalid, invalid: v$.totalAmount.$invalid }"
              v-model.number="v$.totalAmount.$model"
              required
            />
            <div v-if="v$.totalAmount.$anyDirty && v$.totalAmount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.totalAmount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="invoice-type"
              data-cy="type"
              required
            >
              <option v-for="invoiceType in invoiceTypeValues" :key="invoiceType" v-bind:value="invoiceType">{{ invoiceType }}</option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-paymentMethod">Payment Method</label>
            <select
              class="form-control"
              name="paymentMethod"
              :class="{ valid: !v$.paymentMethod.$invalid, invalid: v$.paymentMethod.$invalid }"
              v-model="v$.paymentMethod.$model"
              id="invoice-paymentMethod"
              data-cy="paymentMethod"
              required
            >
              <option v-for="paymentMethod in paymentMethodValues" :key="paymentMethod" v-bind:value="paymentMethod">
                {{ paymentMethod }}
              </option>
            </select>
            <div v-if="v$.paymentMethod.$anyDirty && v$.paymentMethod.$invalid">
              <small class="form-text text-danger" v-for="error of v$.paymentMethod.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="invoice-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="invoice-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="invoice-createdDate"
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
            <label class="form-control-label" for="invoice-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="invoice-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="invoice-lastModifiedDate"
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
            <label class="form-control-label" for="invoice-status">Status</label>
            <select class="form-control" id="invoice-status" data-cy="status" name="status" v-model="invoice.status" required>
              <option v-if="!invoice.status" v-bind:value="null" selected></option>
              <option
                v-bind:value="invoice.status && invoiceStatusOption.id === invoice.status.id ? invoice.status : invoiceStatusOption"
                v-for="invoiceStatusOption in invoiceStatuses"
                :key="invoiceStatusOption.id"
              >
                {{ invoiceStatusOption.statusCode }}
              </option>
            </select>
          </div>
          <div v-if="v$.status.$anyDirty && v$.status.$invalid">
            <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="invoice-order">Order</label>
            <select class="form-control" id="invoice-order" data-cy="order" name="order" v-model="invoice.order" required>
              <option v-if="!invoice.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="invoice.order && orderOption.id === invoice.order.id ? invoice.order : orderOption"
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
<script lang="ts" src="./invoice-update.component.ts"></script>
