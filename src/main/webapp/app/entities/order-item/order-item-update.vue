<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.orderItem.home.createOrEditLabel" data-cy="OrderItemCreateUpdateHeading">Create or edit a Order Item</h2>
        <div>
          <div class="form-group" v-if="orderItem.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="orderItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-orderedQty">Ordered Qty</label>
            <input
              type="number"
              class="form-control"
              name="orderedQty"
              id="order-item-orderedQty"
              data-cy="orderedQty"
              :class="{ valid: !v$.orderedQty.$invalid, invalid: v$.orderedQty.$invalid }"
              v-model.number="v$.orderedQty.$model"
              required
            />
            <div v-if="v$.orderedQty.$anyDirty && v$.orderedQty.$invalid">
              <small class="form-text text-danger" v-for="error of v$.orderedQty.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-receivedQty">Received Qty</label>
            <input
              type="number"
              class="form-control"
              name="receivedQty"
              id="order-item-receivedQty"
              data-cy="receivedQty"
              :class="{ valid: !v$.receivedQty.$invalid, invalid: v$.receivedQty.$invalid }"
              v-model.number="v$.receivedQty.$model"
              required
            />
            <div v-if="v$.receivedQty.$anyDirty && v$.receivedQty.$invalid">
              <small class="form-text text-danger" v-for="error of v$.receivedQty.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-unitPrice">Unit Price</label>
            <input
              type="number"
              class="form-control"
              name="unitPrice"
              id="order-item-unitPrice"
              data-cy="unitPrice"
              :class="{ valid: !v$.unitPrice.$invalid, invalid: v$.unitPrice.$invalid }"
              v-model.number="v$.unitPrice.$model"
              required
            />
            <div v-if="v$.unitPrice.$anyDirty && v$.unitPrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.unitPrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="order-item-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="order-item-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="order-item-createdDate"
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
            <label class="form-control-label" for="order-item-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="order-item-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="order-item-lastModifiedDate"
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
            <label class="form-control-label" for="order-item-productCategory">Product Category</label>
            <select
              class="form-control"
              id="order-item-productCategory"
              data-cy="productCategory"
              name="productCategory"
              v-model="orderItem.productCategory"
              required
            >
              <option v-if="!orderItem.productCategory" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  orderItem.productCategory && productCategoryOption.id === orderItem.productCategory.id
                    ? orderItem.productCategory
                    : productCategoryOption
                "
                v-for="productCategoryOption in productCategories"
                :key="productCategoryOption.id"
              >
                {{ productCategoryOption.name }}
              </option>
            </select>
          </div>
          <div v-if="v$.productCategory.$anyDirty && v$.productCategory.$invalid">
            <small class="form-text text-danger" v-for="error of v$.productCategory.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="order-item-order">Order</label>
            <select class="form-control" id="order-item-order" data-cy="order" name="order" v-model="orderItem.order" required>
              <option v-if="!orderItem.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="orderItem.order && orderOption.id === orderItem.order.id ? orderItem.order : orderOption"
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
<script lang="ts" src="./order-item-update.component.ts"></script>
