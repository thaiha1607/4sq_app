<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="shipmentItem">
        <h2 class="jh-entity-heading" data-cy="shipmentItemDetailsHeading"><span>Shipment Item</span> {{ shipmentItem.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Qty</span>
          </dt>
          <dd>
            <span>{{ shipmentItem.qty }}</span>
          </dd>
          <dt>
            <span>Total</span>
          </dt>
          <dd>
            <span>{{ shipmentItem.total }}</span>
          </dd>
          <dt>
            <span>Roll Qty</span>
          </dt>
          <dd>
            <span>{{ shipmentItem.rollQty }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ shipmentItem.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="shipmentItem.createdDate">{{ formatDateLong(shipmentItem.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ shipmentItem.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="shipmentItem.lastModifiedDate">{{ formatDateLong(shipmentItem.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Order Item</span>
          </dt>
          <dd>
            <div v-if="shipmentItem.orderItem">
              <router-link :to="{ name: 'OrderItemView', params: { orderItemId: shipmentItem.orderItem.id } }">{{
                shipmentItem.orderItem.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Shipment</span>
          </dt>
          <dd>
            <div v-if="shipmentItem.shipment">
              <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipmentItem.shipment.id } }">{{
                shipmentItem.shipment.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="shipmentItem.id"
          :to="{ name: 'ShipmentItemEdit', params: { shipmentItemId: shipmentItem.id } }"
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

<script lang="ts" src="./shipment-item-details.component.ts"></script>
