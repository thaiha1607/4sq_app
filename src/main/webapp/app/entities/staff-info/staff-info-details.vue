<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="staffInfo">
        <h2 class="jh-entity-heading" data-cy="staffInfoDetailsHeading"><span>Staff Info</span> {{ staffInfo.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ staffInfo.status }}</span>
          </dd>
          <dt>
            <span>Role</span>
          </dt>
          <dd>
            <span>{{ staffInfo.role }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ staffInfo.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="staffInfo.createdDate">{{ formatDateLong(staffInfo.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ staffInfo.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="staffInfo.lastModifiedDate">{{ formatDateLong(staffInfo.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ staffInfo.user ? staffInfo.user.login : '' }}
          </dd>
          <dt>
            <span>Working Unit</span>
          </dt>
          <dd>
            <div v-if="staffInfo.workingUnit">
              <router-link :to="{ name: 'WorkingUnitView', params: { workingUnitId: staffInfo.workingUnit.id } }">{{
                staffInfo.workingUnit.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="staffInfo.id"
          :to="{ name: 'StaffInfoEdit', params: { staffInfoId: staffInfo.id } }"
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

<script lang="ts" src="./staff-info-details.component.ts"></script>
