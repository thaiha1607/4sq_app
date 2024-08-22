<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="orderHistory">
        <h2 class="jh-entity-heading" data-cy="orderHistoryDetailsHeading"><span>Order History</span> {{ orderHistory.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Comments</span>
          </dt>
          <dd>
            <span>{{ orderHistory.comments }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ orderHistory.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="orderHistory.createdDate">{{ formatDateLong(orderHistory.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ orderHistory.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="orderHistory.lastModifiedDate">{{ formatDateLong(orderHistory.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <div v-if="orderHistory.status">
              <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: orderHistory.status.id } }">{{
                orderHistory.status.statusCode
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Order</span>
          </dt>
          <dd>
            <div v-if="orderHistory.order">
              <router-link :to="{ name: 'OrderView', params: { orderId: orderHistory.order.id } }">{{ orderHistory.order.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="orderHistory.id"
          :to="{ name: 'OrderHistoryEdit', params: { orderHistoryId: orderHistory.id } }"
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

<script lang="ts" src="./order-history-details.component.ts"></script>
