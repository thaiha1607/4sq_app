<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.internalOrder.home.createOrEditLabel" data-cy="InternalOrderCreateUpdateHeading">
          Create or edit a Internal Order
        </h2>
        <div>
          <div class="form-group" v-if="internalOrder.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="internalOrder.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="internal-order-type"
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
            <label class="form-control-label" for="internal-order-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="internal-order-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="internal-order-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="internal-order-createdDate"
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
            <label class="form-control-label" for="internal-order-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="internal-order-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="internal-order-lastModifiedDate"
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
            <label class="form-control-label" for="internal-order-status">Status</label>
            <select class="form-control" id="internal-order-status" data-cy="status" name="status" v-model="internalOrder.status" required>
              <option v-if="!internalOrder.status" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  internalOrder.status && orderStatusOption.id === internalOrder.status.id ? internalOrder.status : orderStatusOption
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
            <label class="form-control-label" for="internal-order-rootOrder">Root Order</label>
            <select
              class="form-control"
              id="internal-order-rootOrder"
              data-cy="rootOrder"
              name="rootOrder"
              v-model="internalOrder.rootOrder"
              required
            >
              <option v-if="!internalOrder.rootOrder" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  internalOrder.rootOrder && orderOption.id === internalOrder.rootOrder.id ? internalOrder.rootOrder : orderOption
                "
                v-for="orderOption in orders"
                :key="orderOption.id"
              >
                {{ orderOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.rootOrder.$anyDirty && v$.rootOrder.$invalid">
            <small class="form-text text-danger" v-for="error of v$.rootOrder.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./internal-order-update.component.ts"></script>
