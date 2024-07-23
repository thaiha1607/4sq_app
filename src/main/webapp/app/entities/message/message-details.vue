<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="message">
        <h2 class="jh-entity-heading" data-cy="messageDetailsHeading"><span>Message</span> {{ message.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Type</span>
          </dt>
          <dd>
            <span>{{ message.type }}</span>
          </dd>
          <dt>
            <span>Content</span>
          </dt>
          <dd>
            <span>{{ message.content }}</span>
          </dd>
          <dt>
            <span>Created By</span>
          </dt>
          <dd>
            <span>{{ message.createdBy }}</span>
          </dd>
          <dt>
            <span>Created Date</span>
          </dt>
          <dd>
            <span v-if="message.createdDate">{{ formatDateLong(message.createdDate) }}</span>
          </dd>
          <dt>
            <span>Last Modified By</span>
          </dt>
          <dd>
            <span>{{ message.lastModifiedBy }}</span>
          </dd>
          <dt>
            <span>Last Modified Date</span>
          </dt>
          <dd>
            <span v-if="message.lastModifiedDate">{{ formatDateLong(message.lastModifiedDate) }}</span>
          </dd>
          <dt>
            <span>Participant</span>
          </dt>
          <dd>
            <div v-if="message.participant">
              <router-link :to="{ name: 'ParticipantView', params: { participantId: message.participant.id } }">{{
                message.participant.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Seen Participant</span>
          </dt>
          <dd>
            <span v-for="(seenParticipant, i) in message.seenParticipants" :key="seenParticipant.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ParticipantView', params: { participantId: seenParticipant.id } }">{{
                seenParticipant.id
              }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Back</span>
        </button>
        <router-link v-if="message.id" :to="{ name: 'MessageEdit', params: { messageId: message.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./message-details.component.ts"></script>
