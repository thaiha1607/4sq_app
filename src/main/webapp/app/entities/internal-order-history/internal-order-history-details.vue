<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="internalOrderHistory">
        <h2 class="jh-entity-heading" data-cy="internalOrderHistoryDetailsHeading">
          <span>Internal Order History</span> {{ internalOrderHistory.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ internalOrderHistory.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ internalOrderHistory.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="internalOrderHistory.createdDate">{{ formatDateLong(internalOrderHistory.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ internalOrderHistory.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="internalOrderHistory.lastModifiedDate">{{ formatDateLong(internalOrderHistory.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <div v-if="internalOrderHistory.status">
              <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: internalOrderHistory.status.id } }">{{
                internalOrderHistory.status.statusCode
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Order</span>
          </dt>
          <dd>
            <div v-if="internalOrderHistory.order">
              <router-link :to="{ name: 'InternalOrderView', params: { internalOrderId: internalOrderHistory.order.id } }">{{
                internalOrderHistory.order.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="internalOrderHistory.id"
          :to="{ name: 'InternalOrderHistoryEdit', params: { internalOrderHistoryId: internalOrderHistory.id } }"
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

<script lang="ts" src="./internal-order-history-details.component.ts"></script>
