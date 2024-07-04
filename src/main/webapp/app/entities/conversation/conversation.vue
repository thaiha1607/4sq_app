<template>
  <div>
    <h2 id="page-heading" data-cy="ConversationHeading">
      <span id="conversation-heading">Conversations</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ConversationCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-conversation"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Create a new Conversation</span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              placeholder="Search for Conversation"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && conversations && conversations.length === 0">
      <span>No Conversations found</span>
    </div>
    <div class="table-responsive" v-if="conversations && conversations.length > 0">
      <table class="table table-striped" aria-describedby="conversations">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Title</span></th>
            <th scope="row"><span>Created By</span></th>
            <th scope="row"><span>Created Date</span></th>
            <th scope="row"><span>Last Modified By</span></th>
            <th scope="row"><span>Last Modified Date</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="conversation in conversations" :key="conversation.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ConversationView', params: { conversationId: conversation.id } }">{{
                conversation.id
              }}</router-link>
            </td>
            <td>{{ conversation.title }}</td>
            <td>{{ conversation.createdBy }}</td>
            <td>{{ formatDateShort(conversation.createdDate) || '' }}</td>
            <td>{{ conversation.lastModifiedBy }}</td>
            <td>{{ formatDateShort(conversation.lastModifiedDate) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ConversationView', params: { conversationId: conversation.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ConversationEdit', params: { conversationId: conversation.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(conversation)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="foursquareApp.conversation.delete.question" data-cy="conversationDeleteDialogHeading">Confirm delete operation</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-conversation-heading">Are you sure you want to delete Conversation {{ removeId }}?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-conversation"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeConversation()"
          >
            Delete
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./conversation.component.ts"></script>
