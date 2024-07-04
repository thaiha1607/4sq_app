<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.productQuantity.home.createOrEditLabel" data-cy="ProductQuantityCreateUpdateHeading">
          Create or edit a Product Quantity
        </h2>
        <div>
          <div class="form-group" v-if="productQuantity.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="productQuantity.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-quantity-qty">Qty</label>
            <input
              type="number"
              class="form-control"
              name="qty"
              id="product-quantity-qty"
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
            <label class="form-control-label" for="product-quantity-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="product-quantity-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-quantity-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="product-quantity-createdDate"
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
            <label class="form-control-label" for="product-quantity-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="product-quantity-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-quantity-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="product-quantity-lastModifiedDate"
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
            <label class="form-control-label" for="product-quantity-workingUnit">Working Unit</label>
            <select
              class="form-control"
              id="product-quantity-workingUnit"
              data-cy="workingUnit"
              name="workingUnit"
              v-model="productQuantity.workingUnit"
              required
            >
              <option v-if="!productQuantity.workingUnit" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productQuantity.workingUnit && workingUnitOption.id === productQuantity.workingUnit.id
                    ? productQuantity.workingUnit
                    : workingUnitOption
                "
                v-for="workingUnitOption in workingUnits"
                :key="workingUnitOption.id"
              >
                {{ workingUnitOption.name }}
              </option>
            </select>
          </div>
          <div v-if="v$.workingUnit.$anyDirty && v$.workingUnit.$invalid">
            <small class="form-text text-danger" v-for="error of v$.workingUnit.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-quantity-productCategory">Product Category</label>
            <select
              class="form-control"
              id="product-quantity-productCategory"
              data-cy="productCategory"
              name="productCategory"
              v-model="productQuantity.productCategory"
              required
            >
              <option v-if="!productQuantity.productCategory" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productQuantity.productCategory && productCategoryOption.id === productQuantity.productCategory.id
                    ? productQuantity.productCategory
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
<script lang="ts" src="./product-quantity-update.component.ts"></script>
