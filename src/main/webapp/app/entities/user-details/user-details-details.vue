<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="userDetails">
        <h2 class="jh-entity-heading" data-cy="userDetailsDetailsHeading"><span>User Details</span> {{ userDetails.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Phone</span>
          </dt>
          <dd>
            <span>{{ userDetails.phone }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ userDetails.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="userDetails.createdDate">{{ formatDateLong(userDetails.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ userDetails.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="userDetails.lastModifiedDate">{{ formatDateLong(userDetails.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ userDetails.user ? userDetails.user.login : '' }}
          </dd>
          <dt>
            <span>Working Unit</span>
          </dt>
          <dd>
            <div v-if="userDetails.workingUnit">
              <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: userDetails.workingUnit.id } }">{{
                userDetails.workingUnit.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="userDetails.id"
          :to="{ name: 'UserDetailsEdit', params: { userDetailsId: userDetails.id } }"
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

<script lang="ts" src="./user-details-details.component.ts"></script>
