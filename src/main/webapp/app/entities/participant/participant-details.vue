<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="participant">
        <h2 class="jh-entity-heading" data-cy="participantDetailsHeading"><span>Participant</span> {{ participant.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Is Admin</span>
          </dt>
          <dd>
            <span>{{ participant.isAdmin }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ participant.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="participant.createdDate">{{ formatDateLong(participant.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ participant.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="participant.lastModifiedDate">{{ formatDateLong(participant.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ participant.user ? participant.user.login : '' }}
          </dd>
          <dt>
            <span>Conversation</span>
          </dt>
          <dd>
            <div v-if="participant.conversation">
              <router-link :to="{ name: 'ConversationView', params: { conversationId: participant.conversation.id } }">{{
                participant.conversation.title
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Seen Message</span>
          </dt>
          <dd>
            <span v-for="(seenMessage, i) in participant.seenMessages" :key="seenMessage.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'MessageView', params: { messageId: seenMessage.id } }">{{ seenMessage.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link
          v-if="participant.id"
          :to="{ name: 'ParticipantEdit', params: { participantId: participant.id } }"
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

<script lang="ts" src="./participant-details.component.ts"></script>
