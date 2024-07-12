<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="productCategory">
        <h2 class="jh-entity-heading" data-cy="productCategoryDetailsHeading"><span>Product Category</span> {{ productCategory.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ productCategory.name }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ productCategory.description }}</span>
          </dd>
          <dt>
            <span>Image Uri</span>
          </dt>
          <dd>
            <span>{{ productCategory.imageUri }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ productCategory.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="productCategory.createdDate">{{ formatDateLong(productCategory.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ productCategory.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="productCategory.lastModifiedDate">{{ formatDateLong(productCategory.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Colour</span>
          </dt>
          <dd>
            <div v-if="productCategory.colour">
              <router-link :to="{ name: 'ColourView', params: { colourId: productCategory.colour.id } }">{{
                productCategory.colour.hexCode
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Product</span>
          </dt>
          <dd>
            <div v-if="productCategory.product">
              <router-link :to="{ name: 'ProductView', params: { productId: productCategory.product.id } }">{{
                productCategory.product.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="productCategory.id"
          :to="{ name: 'ProductCategoryEdit', params: { productCategoryId: productCategory.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./product-category-details.component.ts"></script>
