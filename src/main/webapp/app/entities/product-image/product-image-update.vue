<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.productImage.home.createOrEditLabel" data-cy="ProductImageCreateUpdateHeading">
          Create or edit a Product Image
        </h2>
        <div>
          <div class="form-group" v-if="productImage.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="productImage.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-image-imageUri">Image Uri</label>
            <input
              type="text"
              class="form-control"
              name="imageUri"
              id="product-image-imageUri"
              data-cy="imageUri"
              :class="{ valid: !v$.imageUri.$invalid, invalid: v$.imageUri.$invalid }"
              v-model="v$.imageUri.$model"
              required
            />
            <div v-if="v$.imageUri.$anyDirty && v$.imageUri.$invalid">
              <small class="form-text text-danger" v-for="error of v$.imageUri.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-image-altText">Alt Text</label>
            <input
              type="text"
              class="form-control"
              name="altText"
              id="product-image-altText"
              data-cy="altText"
              :class="{ valid: !v$.altText.$invalid, invalid: v$.altText.$invalid }"
              v-model="v$.altText.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-image-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="product-image-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-image-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="product-image-createdDate"
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
            <label class="form-control-label" for="product-image-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="product-image-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-image-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="product-image-lastModifiedDate"
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
            <label class="form-control-label" for="product-image-product">Product</label>
            <select
              class="form-control"
              id="product-image-product"
              data-cy="product"
              name="product"
              v-model="productImage.product"
              required
            >
              <option v-if="!productImage.product" v-bind:value="null" selected></option>
              <option
                v-bind:value="productImage.product && productOption.id === productImage.product.id ? productImage.product : productOption"
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.product.$anyDirty && v$.product.$invalid">
            <small class="form-text text-danger" v-for="error of v$.product.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./product-image-update.component.ts"></script>
