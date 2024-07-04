<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="colour">
        <h2 class="jh-entity-heading" data-cy="colourDetailsHeading"><span>Colour</span> {{ colour.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ colour.name }}</span>
          </dd>
          <dt>
            <span>Hex Code</span>
          </dt>
          <dd>
            <span>{{ colour.hexCode }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ colour.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="colour.createdDate">{{ formatDateLong(colour.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ colour.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="colour.lastModifiedDate">{{ formatDateLong(colour.lastModifiedDate) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="colour.id" :to="{ name: 'ColourEdit', params: { colourId: colour.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./colour-details.component.ts"></script>
