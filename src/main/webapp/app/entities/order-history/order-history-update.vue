<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.orderHistory.home.createOrEditLabel" data-cy="OrderHistoryCreateUpdateHeading">
          Create or edit a Order History
        </h2>
        <div>
          <div class="form-group" v-if="orderHistory.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="orderHistory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-history-comments">Comments</label>
            <input
              type="text"
              class="form-control"
              name="comments"
              id="order-history-comments"
              data-cy="comments"
              :class="{ valid: !v$.comments.$invalid, invalid: v$.comments.$invalid }"
              v-model="v$.comments.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-history-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="order-history-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-history-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="order-history-createdDate"
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
            <label class="form-control-label" for="order-history-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="order-history-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-history-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="order-history-lastModifiedDate"
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
            <label class="form-control-label" for="order-history-status">Status</label>
            <select class="form-control" id="order-history-status" data-cy="status" name="status" v-model="orderHistory.status" required>
              <option v-if="!orderHistory.status" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  orderHistory.status && orderStatusOption.id === orderHistory.status.id ? orderHistory.status : orderStatusOption
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
            <label class="form-control-label" for="order-history-order">Order</label>
            <select class="form-control" id="order-history-order" data-cy="order" name="order" v-model="orderHistory.order" required>
              <option v-if="!orderHistory.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="orderHistory.order && orderOption.id === orderHistory.order.id ? orderHistory.order : orderOption"
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
<script lang="ts" src="./order-history-update.component.ts"></script>
