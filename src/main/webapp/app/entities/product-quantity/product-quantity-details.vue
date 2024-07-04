<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="productQuantity">
        <h2 class="jh-entity-heading" data-cy="productQuantityDetailsHeading"><span>Product Quantity</span> {{ productQuantity.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Qty</span>
          </dt>
          <dd>
            <span>{{ productQuantity.qty }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ productQuantity.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="productQuantity.createdDate">{{ formatDateLong(productQuantity.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ productQuantity.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="productQuantity.lastModifiedDate">{{ formatDateLong(productQuantity.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Working Unit</span>
          </dt>
          <dd>
            <div v-if="productQuantity.workingUnit">
              <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: productQuantity.workingUnit.id } }">{{
                productQuantity.workingUnit.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Product Category</span>
          </dt>
          <dd>
            <div v-if="productQuantity.productCategory">
              <router-link :to="{ name: 'ProductCategoryView', params: { productCategoryId: productQuantity.productCategory.id } }">{{
                productQuantity.productCategory.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="productQuantity.id"
          :to="{ name: 'ProductQuantityEdit', params: { productQuantityId: productQuantity.id } }"
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

<script lang="ts" src="./product-quantity-details.component.ts"></script>
