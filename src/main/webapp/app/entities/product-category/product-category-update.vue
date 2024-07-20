<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.productCategory.home.createOrEditLabel" data-cy="ProductCategoryCreateUpdateHeading">
          Create or edit a Product Category
        </h2>
        <div>
          <div class="form-group" v-if="productCategory.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="productCategory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-category-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="product-category-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-category-imageUri">Image Uri</label>
            <input
              type="text"
              class="form-control"
              name="imageUri"
              id="product-category-imageUri"
              data-cy="imageUri"
              :class="{ valid: !v$.imageUri.$invalid, invalid: v$.imageUri.$invalid }"
              v-model="v$.imageUri.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-category-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="product-category-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-category-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="product-category-createdDate"
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
            <label class="form-control-label" for="product-category-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="product-category-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-category-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="product-category-lastModifiedDate"
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
            <label class="form-control-label" for="product-category-colour">Colour</label>
            <select
              class="form-control"
              id="product-category-colour"
              data-cy="colour"
              name="colour"
              v-model="productCategory.colour"
              required
            >
              <option v-if="!productCategory.colour" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productCategory.colour && colourOption.id === productCategory.colour.id ? productCategory.colour : colourOption
                "
                v-for="colourOption in colours"
                :key="colourOption.id"
              >
                {{ colourOption.hexCode }}
              </option>
            </select>
          </div>
          <div v-if="v$.colour.$anyDirty && v$.colour.$invalid">
            <small class="form-text text-danger" v-for="error of v$.colour.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="product-category-product">Product</label>
            <select
              class="form-control"
              id="product-category-product"
              data-cy="product"
              name="product"
              v-model="productCategory.product"
              required
            >
              <option v-if="!productCategory.product" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  productCategory.product && productOption.id === productCategory.product.id ? productCategory.product : productOption
                "
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.name }}
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
<script lang="ts" src="./product-category-update.component.ts"></script>
