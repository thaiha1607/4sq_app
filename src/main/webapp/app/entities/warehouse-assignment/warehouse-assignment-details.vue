<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="warehouseAssignment">
        <h2 class="jh-entity-heading" data-cy="warehouseAssignmentDetailsHeading">
          <span>Warehouse Assignment</span> {{ warehouseAssignment.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ warehouseAssignment.status }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ warehouseAssignment.note }}</span>
          </dd>
          <dt>
            <span>Other Info</span>
          </dt>
          <dd>
            <span>{{ warehouseAssignment.otherInfo }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ warehouseAssignment.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="warehouseAssignment.createdDate">{{ formatDateLong(warehouseAssignment.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ warehouseAssignment.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="warehouseAssignment.lastModifiedDate">{{ formatDateLong(warehouseAssignment.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ warehouseAssignment.user ? warehouseAssignment.user.login : '' }}
          </dd>
          <dt>
            <span>Source Working Unit</span>
          </dt>
          <dd>
            <div v-if="warehouseAssignment.sourceWorkingUnit">
              <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: warehouseAssignment.sourceWorkingUnit.id } }">{{
                warehouseAssignment.sourceWorkingUnit.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Target Working Unit</span>
          </dt>
          <dd>
            <div v-if="warehouseAssignment.targetWorkingUnit">
              <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: warehouseAssignment.targetWorkingUnit.id } }">{{
                warehouseAssignment.targetWorkingUnit.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Internal Order</span>
          </dt>
          <dd>
            <div v-if="warehouseAssignment.internalOrder">
              <router-link :to="{ name: 'InternalOrderView', params: { internalOrderId: warehouseAssignment.internalOrder.id } }">{{
                warehouseAssignment.internalOrder.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="warehouseAssignment.id"
          :to="{ name: 'WarehouseAssignmentEdit', params: { warehouseAssignmentId: warehouseAssignment.id } }"
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

<script lang="ts" src="./warehouse-assignment-details.component.ts"></script>
