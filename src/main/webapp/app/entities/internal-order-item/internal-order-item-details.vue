<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="internalOrderItem">
        <h2 class="jh-entity-heading" data-cy="internalOrderItemDetailsHeading">
          <span>Internal Order Item</span> {{ internalOrderItem.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Qty</span>
          </dt>
          <dd>
            <span>{{ internalOrderItem.qty }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ internalOrderItem.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ internalOrderItem.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="internalOrderItem.createdDate">{{ formatDateLong(internalOrderItem.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ internalOrderItem.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="internalOrderItem.lastModifiedDate">{{ formatDateLong(internalOrderItem.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Order Item</span>
          </dt>
          <dd>
            <div v-if="internalOrderItem.orderItem">
              <router-link :to="{ name: 'OrderItemView', params: { orderItemId: internalOrderItem.orderItem.id } }">{{
                internalOrderItem.orderItem.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="internalOrderItem.id"
          :to="{ name: 'InternalOrderItemEdit', params: { internalOrderItemId: internalOrderItem.id } }"
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

<script lang="ts" src="./internal-order-item-details.component.ts"></script>
