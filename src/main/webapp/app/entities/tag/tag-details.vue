<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="tag">
        <h2 class="jh-entity-heading" data-cy="tagDetailsHeading"><span>Tag</span> {{ tag.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ tag.name }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ tag.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="tag.createdDate">{{ formatDateLong(tag.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ tag.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="tag.lastModifiedDate">{{ formatDateLong(tag.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Product</span>
          </dt>
          <dd>
            <span v-for="(product, i) in tag.products" :key="product.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ProductView', params: { productId: product.id } }">{{ product.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="tag.id" :to="{ name: 'TagEdit', params: { tagId: tag.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./tag-details.component.ts"></script>
