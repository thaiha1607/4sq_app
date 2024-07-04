<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="workingUnit">
        <h2 class="jh-entity-heading" data-cy="workingUnitDetailsHeading"><span>Working Unit</span> {{ workingUnit.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ workingUnit.name }}</span>
          </dd>
          <dt>
            <span>Type</span>
          </dt>
          <dd>
            <span>{{ workingUnit.type }}</span>
          </dd>
          <dt>
            <span>Image Uri</span>
          </dt>
          <dd>
            <span>{{ workingUnit.imageUri }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ workingUnit.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="workingUnit.createdDate">{{ formatDateLong(workingUnit.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ workingUnit.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="workingUnit.lastModifiedDate">{{ formatDateLong(workingUnit.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Address</span>
          </dt>
          <dd>
            <div v-if="workingUnit.address">
              <router-link :to="{ name: 'AddressView', params: { addressId: workingUnit.address.id } }">{{
                workingUnit.address.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="workingUnit.id"
          :to="{ name: 'WorkingUnitEdit', params: { workingUnitId: workingUnit.id } }"
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

<script lang="ts" src="./working-unit-details.component.ts"></script>
