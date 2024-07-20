<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.order.home.createOrEditLabel" data-cy="OrderCreateUpdateHeading">Create or edit a Order</h2>
        <div>
          <div class="form-group" v-if="order.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="order.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="order-type"
              data-cy="type"
              required
            >
              <option v-for="orderType in orderTypeValues" :key="orderType" v-bind:value="orderType">{{ orderType }}</option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-priority">Priority</label>
            <input
              type="number"
              class="form-control"
              name="priority"
              id="order-priority"
              data-cy="priority"
              :class="{ valid: !v$.priority.$invalid, invalid: v$.priority.$invalid }"
              v-model.number="v$.priority.$model"
            />
            <div v-if="v$.priority.$anyDirty && v$.priority.$invalid">
              <small class="form-text text-danger" v-for="error of v$.priority.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-isInternal">Is Internal</label>
            <input
              type="checkbox"
              class="form-check"
              name="isInternal"
              id="order-isInternal"
              data-cy="isInternal"
              :class="{ valid: !v$.isInternal.$invalid, invalid: v$.isInternal.$invalid }"
              v-model="v$.isInternal.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="order-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="order-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="order-createdDate"
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
            <label class="form-control-label" for="order-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="order-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="order-lastModifiedDate"
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
            <label class="form-control-label" for="order-customer">Customer</label>
            <select class="form-control" id="order-customer" data-cy="customer" name="customer" v-model="order.customer" required>
              <option v-if="!order.customer" v-bind:value="null" selected></option>
              <option
                v-bind:value="order.customer && userOption.id === order.customer.id ? order.customer : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="v$.customer.$anyDirty && v$.customer.$invalid">
            <small class="form-text text-danger" v-for="error of v$.customer.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-status">Status</label>
            <select class="form-control" id="order-status" data-cy="status" name="status" v-model="order.status" required>
              <option v-if="!order.status" v-bind:value="null" selected></option>
              <option
                v-bind:value="order.status && orderStatusOption.statusCode === order.status.statusCode ? order.status : orderStatusOption"
                v-for="orderStatusOption in orderStatuses"
                :key="orderStatusOption.statusCode"
              >
                {{ orderStatusOption.description }}
              </option>
            </select>
          </div>
          <div v-if="v$.status.$anyDirty && v$.status.$invalid">
            <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-address">Address</label>
            <select class="form-control" id="order-address" data-cy="address" name="address" v-model="order.address">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="order.address && addressOption.id === order.address.id ? order.address : addressOption"
                v-for="addressOption in addresses"
                :key="addressOption.id"
              >
                {{ addressOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-parentOrder">Parent Order</label>
            <select class="form-control" id="order-parentOrder" data-cy="parentOrder" name="parentOrder" v-model="order.parentOrder">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="order.parentOrder && orderOption.id === order.parentOrder.id ? order.parentOrder : orderOption"
                v-for="orderOption in orders"
                :key="orderOption.id"
              >
                {{ orderOption.id }}
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
<script lang="ts" src="./order-update.component.ts"></script>
