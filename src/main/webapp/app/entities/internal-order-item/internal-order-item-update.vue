<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.internalOrderItem.home.createOrEditLabel" data-cy="InternalOrderItemCreateUpdateHeading">
          Create or edit a Internal Order Item
        </h2>
        <div>
          <div class="form-group" v-if="internalOrderItem.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="internalOrderItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-item-qty">Qty</label>
            <input
              type="number"
              class="form-control"
              name="qty"
              id="internal-order-item-qty"
              data-cy="qty"
              :class="{ valid: !v$.qty.$invalid, invalid: v$.qty.$invalid }"
              v-model.number="v$.qty.$model"
              required
            />
            <div v-if="v$.qty.$anyDirty && v$.qty.$invalid">
              <small class="form-text text-danger" v-for="error of v$.qty.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-item-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="internal-order-item-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-item-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="internal-order-item-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-item-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="internal-order-item-createdDate"
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
            <label class="form-control-label" for="internal-order-item-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="internal-order-item-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="internal-order-item-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="internal-order-item-lastModifiedDate"
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
            <label class="form-control-label" for="internal-order-item-orderItem">Order Item</label>
            <select
              class="form-control"
              id="internal-order-item-orderItem"
              data-cy="orderItem"
              name="orderItem"
              v-model="internalOrderItem.orderItem"
              required
            >
              <option v-if="!internalOrderItem.orderItem" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  internalOrderItem.orderItem && orderItemOption.id === internalOrderItem.orderItem.id
                    ? internalOrderItem.orderItem
                    : orderItemOption
                "
                v-for="orderItemOption in orderItems"
                :key="orderItemOption.id"
              >
                {{ orderItemOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.orderItem.$anyDirty && v$.orderItem.$invalid">
            <small class="form-text text-danger" v-for="error of v$.orderItem.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./internal-order-item-update.component.ts"></script>
