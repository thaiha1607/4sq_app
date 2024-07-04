<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="invoice">
        <h2 class="jh-entity-heading" data-cy="invoiceDetailsHeading"><span>Invoice</span> {{ invoice.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Total Amount</span>
          </dt>
          <dd>
            <span>{{ invoice.totalAmount }}</span>
          </dd>
          <dt>
            <span>Type</span>
          </dt>
          <dd>
            <span>{{ invoice.type }}</span>
          </dd>
          <dt>
            <span>Payment Method</span>
          </dt>
          <dd>
            <span>{{ invoice.paymentMethod }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ invoice.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ invoice.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="invoice.createdDate">{{ formatDateLong(invoice.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ invoice.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="invoice.lastModifiedDate">{{ formatDateLong(invoice.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <div v-if="invoice.status">
              <router-link :to="{ name: 'InvoiceStatusView', params: { invoiceStatusId: invoice.status.statusCode } }">{{
                invoice.status.description
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Order</span>
          </dt>
          <dd>
            <div v-if="invoice.order">
              <router-link :to="{ name: 'OrderView', params: { orderId: invoice.order.id } }">{{ invoice.order.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="invoice.id" :to="{ name: 'InvoiceEdit', params: { invoiceId: invoice.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./invoice-details.component.ts"></script>
