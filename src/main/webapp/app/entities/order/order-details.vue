<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="order">
        <h2 class="jh-entity-heading" data-cy="orderDetailsHeading"><span>Order</span> {{ order.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Type</span>
          </dt>
          <dd>
            <span>{{ order.type }}</span>
          </dd>
          <dt>
            <span>Priority</span>
          </dt>
          <dd>
            <span>{{ order.priority }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ order.note }}</span>
          </dd>
          <dt>
            <span>Other Info</span>
          </dt>
          <dd>
            <span>{{ order.otherInfo }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ order.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="order.createdDate">{{ formatDateLong(order.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ order.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="order.lastModifiedDate">{{ formatDateLong(order.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Customer</span>
          </dt>
          <dd>
            {{ order.customer ? order.customer.login : '' }}
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <div v-if="order.status">
              <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: order.status.id } }">{{
                order.status.statusCode
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Address</span>
          </dt>
          <dd>
            <div v-if="order.address">
              <router-link :to="{ name: 'AddressView', params: { addressId: order.address.id } }">{{ order.address.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Root Order</span>
          </dt>
          <dd>
            <div v-if="order.rootOrder">
              <router-link :to="{ name: 'OrderView', params: { orderId: order.rootOrder.id } }">{{ order.rootOrder.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="order.id" :to="{ name: 'OrderEdit', params: { orderId: order.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./order-details.component.ts"></script>
