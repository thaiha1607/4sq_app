<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="internalOrder">
        <h2 class="jh-entity-heading" data-cy="internalOrderDetailsHeading"><span>Internal Order</span> {{ internalOrder.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Type</span>
          </dt>
          <dd>
            <span>{{ internalOrder.type }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ internalOrder.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ internalOrder.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="internalOrder.createdDate">{{ formatDateLong(internalOrder.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ internalOrder.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="internalOrder.lastModifiedDate">{{ formatDateLong(internalOrder.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <div v-if="internalOrder.status">
              <router-link :to="{ name: 'OrderStatusView', params: { orderStatusId: internalOrder.status.id } }">{{
                internalOrder.status.statusCode
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Root Order</span>
          </dt>
          <dd>
            <div v-if="internalOrder.rootOrder">
              <router-link :to="{ name: 'OrderView', params: { orderId: internalOrder.rootOrder.id } }">{{
                internalOrder.rootOrder.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="internalOrder.id"
          :to="{ name: 'InternalOrderEdit', params: { internalOrderId: internalOrder.id } }"
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

<script lang="ts" src="./internal-order-details.component.ts"></script>
