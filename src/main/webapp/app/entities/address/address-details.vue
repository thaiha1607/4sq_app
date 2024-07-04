<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="address">
        <h2 class="jh-entity-heading" data-cy="addressDetailsHeading"><span>Address</span> {{ address.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Line 1</span>
          </dt>
          <dd>
            <span>{{ address.line1 }}</span>
          </dd>
          <dt>
            <span>Line 2</span>
          </dt>
          <dd>
            <span>{{ address.line2 }}</span>
          </dd>
          <dt>
            <span>City</span>
          </dt>
          <dd>
            <span>{{ address.city }}</span>
          </dd>
          <dt>
            <span>State</span>
          </dt>
          <dd>
            <span>{{ address.state }}</span>
          </dd>
          <dt>
            <span>Country</span>
          </dt>
          <dd>
            <span>{{ address.country }}</span>
          </dd>
          <dt>
            <span>Zip Or Postal Code</span>
          </dt>
          <dd>
            <span>{{ address.zipOrPostalCode }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ address.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="address.createdDate">{{ formatDateLong(address.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ address.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="address.lastModifiedDate">{{ formatDateLong(address.lastModifiedDate) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="address.id" :to="{ name: 'AddressEdit', params: { addressId: address.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./address-details.component.ts"></script>
