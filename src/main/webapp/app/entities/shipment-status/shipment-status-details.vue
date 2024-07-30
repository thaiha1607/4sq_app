<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="shipmentStatus">
        <h2 class="jh-entity-heading" data-cy="shipmentStatusDetailsHeading"><span>Shipment Status</span> {{ shipmentStatus.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Status Code</span>
          </dt>
          <dd>
            <span>{{ shipmentStatus.statusCode }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ shipmentStatus.description }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ shipmentStatus.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="shipmentStatus.createdDate">{{ formatDateLong(shipmentStatus.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ shipmentStatus.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="shipmentStatus.lastModifiedDate">{{ formatDateLong(shipmentStatus.lastModifiedDate) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="shipmentStatus.id"
          :to="{ name: 'ShipmentStatusEdit', params: { shipmentStatusId: shipmentStatus.id } }"
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

<script lang="ts" src="./shipment-status-details.component.ts"></script>
