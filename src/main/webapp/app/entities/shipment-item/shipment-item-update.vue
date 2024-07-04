<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.shipmentItem.home.createOrEditLabel" data-cy="ShipmentItemCreateUpdateHeading">
          Create or edit a Shipment Item
        </h2>
        <div>
          <div class="form-group" v-if="shipmentItem.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shipmentItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-item-qty">Qty</label>
            <input
              type="number"
              class="form-control"
              name="qty"
              id="shipment-item-qty"
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
            <label class="form-control-label" for="shipment-item-total">Total</label>
            <input
              type="number"
              class="form-control"
              name="total"
              id="shipment-item-total"
              data-cy="total"
              :class="{ valid: !v$.total.$invalid, invalid: v$.total.$invalid }"
              v-model.number="v$.total.$model"
              required
            />
            <div v-if="v$.total.$anyDirty && v$.total.$invalid">
              <small class="form-text text-danger" v-for="error of v$.total.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-item-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="shipment-item-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-item-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="shipment-item-createdDate"
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
            <label class="form-control-label" for="shipment-item-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="shipment-item-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-item-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="shipment-item-lastModifiedDate"
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
            <label class="form-control-label" for="shipment-item-orderItem">Order Item</label>
            <select
              class="form-control"
              id="shipment-item-orderItem"
              data-cy="orderItem"
              name="orderItem"
              v-model="shipmentItem.orderItem"
              required
            >
              <option v-if="!shipmentItem.orderItem" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  shipmentItem.orderItem && orderItemOption.id === shipmentItem.orderItem.id ? shipmentItem.orderItem : orderItemOption
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
          <div class="form-group">
            <label class="form-control-label" for="shipment-item-shipment">Shipment</label>
            <select
              class="form-control"
              id="shipment-item-shipment"
              data-cy="shipment"
              name="shipment"
              v-model="shipmentItem.shipment"
              required
            >
              <option v-if="!shipmentItem.shipment" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  shipmentItem.shipment && shipmentOption.id === shipmentItem.shipment.id ? shipmentItem.shipment : shipmentOption
                "
                v-for="shipmentOption in shipments"
                :key="shipmentOption.id"
              >
                {{ shipmentOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.shipment.$anyDirty && v$.shipment.$invalid">
            <small class="form-text text-danger" v-for="error of v$.shipment.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./shipment-item-update.component.ts"></script>
