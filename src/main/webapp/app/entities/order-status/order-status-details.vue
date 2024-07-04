<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="orderStatus">
        <h2 class="jh-entity-heading" data-cy="orderStatusDetailsHeading"><span>Order Status</span> {{ orderStatus.statusCode }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ orderStatus.description }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ orderStatus.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="orderStatus.createdDate">{{ formatDateLong(orderStatus.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ orderStatus.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="orderStatus.lastModifiedDate">{{ formatDateLong(orderStatus.lastModifiedDate) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="orderStatus.statusCode"
          :to="{ name: 'OrderStatusEdit', params: { orderStatusId: orderStatus.statusCode } }"
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

<script lang="ts" src="./order-status-details.component.ts"></script>
