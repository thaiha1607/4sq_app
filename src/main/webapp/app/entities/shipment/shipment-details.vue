<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="shipment">
        <h2 class="jh-entity-heading" data-cy="shipmentDetailsHeading"><span>Shipment</span> {{ shipment.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Type</span>
          </dt>
          <dd>
            <span>{{ shipment.type }}</span>
          </dd>
          <dt>
            <span>Shipment Date</span>
          </dt>
          <dd>
            <span v-if="shipment.shipmentDate">{{ formatDateLong(shipment.shipmentDate) }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ shipment.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ shipment.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="shipment.createdDate">{{ formatDateLong(shipment.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ shipment.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="shipment.lastModifiedDate">{{ formatDateLong(shipment.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <div v-if="shipment.status">
              <router-link :to="{ name: 'ShipmentStatusView', params: { shipmentStatusId: shipment.status.statusCode } }">{{
                shipment.status.description
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Order</span>
          </dt>
          <dd>
            <div v-if="shipment.order">
              <router-link :to="{ name: 'OrderView', params: { orderId: shipment.order.id } }">{{ shipment.order.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Invoice</span>
          </dt>
          <dd>
            <div v-if="shipment.invoice">
              <router-link :to="{ name: 'InvoiceView', params: { invoiceId: shipment.invoice.id } }">{{ shipment.invoice.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="shipment.id" :to="{ name: 'ShipmentEdit', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./shipment-details.component.ts"></script>
