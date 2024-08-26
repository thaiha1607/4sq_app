<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.internalOrderHistory.home.createOrEditLabel" data-cy="InternalOrderHistoryCreateUpdateHeading">
          Create or edit a Internal Order History
        </h2>
        <div>
          <div class="form-group" v-if="internalOrderHistory.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="internalOrderHistory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-history-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="internal-order-history-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-history-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="internal-order-history-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-history-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="internal-order-history-createdDate"
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
            <label class="form-control-label" for="internal-order-history-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="internal-order-history-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-history-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="internal-order-history-lastModifiedDate"
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
            <label class="form-control-label" for="internal-order-history-status">Status</label>
            <select
              class="form-control"
              id="internal-order-history-status"
              data-cy="status"
              name="status"
              v-model="internalOrderHistory.status"
              required
            >
              <option v-if="!internalOrderHistory.status" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  internalOrderHistory.status && orderStatusOption.id === internalOrderHistory.status.id
                    ? internalOrderHistory.status
                    : orderStatusOption
                "
                v-for="orderStatusOption in orderStatuses"
                :key="orderStatusOption.id"
              >
                {{ orderStatusOption.statusCode }}
              </option>
            </select>
          </div>
          <div v-if="v$.status.$anyDirty && v$.status.$invalid">
            <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-history-order">Order</label>
            <select
              class="form-control"
              id="internal-order-history-order"
              data-cy="order"
              name="order"
              v-model="internalOrderHistory.order"
              required
            >
              <option v-if="!internalOrderHistory.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  internalOrderHistory.order && internalOrderOption.id === internalOrderHistory.order.id
                    ? internalOrderHistory.order
                    : internalOrderOption
                "
                v-for="internalOrderOption in internalOrders"
                :key="internalOrderOption.id"
              >
                {{ internalOrderOption.id }}
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
<script lang="ts" src="./internal-order-history-update.component.ts"></script>
