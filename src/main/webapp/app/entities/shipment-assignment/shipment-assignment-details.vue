<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="shipmentAssignment">
        <h2 class="jh-entity-heading" data-cy="shipmentAssignmentDetailsHeading">
          <span>Shipment Assignment</span> {{ shipmentAssignment.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ shipmentAssignment.status }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ shipmentAssignment.note }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ shipmentAssignment.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="shipmentAssignment.createdDate">{{ formatDateLong(shipmentAssignment.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ shipmentAssignment.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="shipmentAssignment.lastModifiedDate">{{ formatDateLong(shipmentAssignment.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ shipmentAssignment.user ? shipmentAssignment.user.login : '' }}
          </dd>
          <dt>
            <span>Shipment</span>
          </dt>
          <dd>
            <div v-if="shipmentAssignment.shipment">
              <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipmentAssignment.shipment.id } }">{{
                shipmentAssignment.shipment.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="shipmentAssignment.id"
          :to="{ name: 'ShipmentAssignmentEdit', params: { shipmentAssignmentId: shipmentAssignment.id } }"
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

<script lang="ts" src="./shipment-assignment-details.component.ts"></script>
