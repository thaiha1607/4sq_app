<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="orderItem">
        <h2 class="jh-entity-heading" data-cy="orderItemDetailsHeading"><span>Order Item</span> {{ orderItem.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Ordered Qty</span>
          </dt>
          <dd>
            <span>{{ orderItem.orderedQty }}</span>
          </dd>
          <dt>
            <span>Received Qty</span>
          </dt>
          <dd>
            <span>{{ orderItem.receivedQty }}</span>
          </dd>
          <dt>
            <span>Unit Price</span>
          </dt>
          <dd>
            <span>{{ orderItem.unitPrice }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ orderItem.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ orderItem.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="orderItem.createdDate">{{ formatDateLong(orderItem.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ orderItem.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="orderItem.lastModifiedDate">{{ formatDateLong(orderItem.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Product Category</span>
          </dt>
          <dd>
            <div v-if="orderItem.productCategory">
              <router-link :to="{ name: 'ProductCategoryView', params: { productCategoryId: orderItem.productCategory.id } }">{{
                orderItem.productCategory.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Order</span>
          </dt>
          <dd>
            <div v-if="orderItem.order">
              <router-link :to="{ name: 'OrderView', params: { orderId: orderItem.order.id } }">{{ orderItem.order.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="orderItem.id"
          :to="{ name: 'OrderItemEdit', params: { orderItemId: orderItem.id } }"
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

<script lang="ts" src="./order-item-details.component.ts"></script>
