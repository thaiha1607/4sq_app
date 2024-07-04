<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.participant.home.createOrEditLabel" data-cy="ParticipantCreateUpdateHeading">Create or edit a Participant</h2>
        <div>
          <div class="form-group" v-if="participant.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="participant.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-isAdmin">Is Admin</label>
            <input
              type="checkbox"
              class="form-check"
              name="isAdmin"
              id="participant-isAdmin"
              data-cy="isAdmin"
              :class="{ valid: !v$.isAdmin.$invalid, invalid: v$.isAdmin.$invalid }"
              v-model="v$.isAdmin.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="participant-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="participant-createdDate"
                data-cy="createdDate"
                type="datetime-local"
                class="form-control"
                name="createdDate"
                :class="{ valid: !v$.createdDate.$invalid, invalid: v$.createdDate.$invalid }"
                :value="convertDateTimeFromServer(v$.createdDate.$model)"
                @change="updateInstantField('createdDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="participant-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="participant-lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                class="form-control"
                name="lastModifiedDate"
                :class="{ valid: !v$.lastModifiedDate.$invalid, invalid: v$.lastModifiedDate.$invalid }"
                :value="convertDateTimeFromServer(v$.lastModifiedDate.$model)"
                @change="updateInstantField('lastModifiedDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-user">User</label>
            <select class="form-control" id="participant-user" data-cy="user" name="user" v-model="participant.user" required>
              <option v-if="!participant.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="participant.user && userOption.id === participant.user.id ? participant.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="v$.user.$anyDirty && v$.user.$invalid">
            <small class="form-text text-danger" v-for="error of v$.user.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="participant-conversation">Conversation</label>
            <select
              class="form-control"
              id="participant-conversation"
              data-cy="conversation"
              name="conversation"
              v-model="participant.conversation"
              required
            >
              <option v-if="!participant.conversation" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  participant.conversation && conversationOption.id === participant.conversation.id
                    ? participant.conversation
                    : conversationOption
                "
                v-for="conversationOption in conversations"
                :key="conversationOption.id"
              >
                {{ conversationOption.title }}
              </option>
            </select>
          </div>
          <div v-if="v$.conversation.$anyDirty && v$.conversation.$invalid">
            <small class="form-text text-danger" v-for="error of v$.conversation.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./participant-update.component.ts"></script>
