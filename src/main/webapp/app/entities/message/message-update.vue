<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.message.home.createOrEditLabel" data-cy="MessageCreateUpdateHeading">Create or edit a Message</h2>
        <div>
          <div class="form-group" v-if="message.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="message.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="message-type">Type</label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="message-type"
              data-cy="type"
              required
            >
              <option v-for="messageType in messageTypeValues" :key="messageType" v-bind:value="messageType">{{ messageType }}</option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="message-content">Content</label>
            <input
              type="text"
              class="form-control"
              name="content"
              id="message-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="message-isSeen">Is Seen</label>
            <input
              type="checkbox"
              class="form-check"
              name="isSeen"
              id="message-isSeen"
              data-cy="isSeen"
              :class="{ valid: !v$.isSeen.$invalid, invalid: v$.isSeen.$invalid }"
              v-model="v$.isSeen.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="message-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="message-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="message-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="message-createdDate"
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
            <label class="form-control-label" for="message-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="message-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="message-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="message-lastModifiedDate"
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
            <label class="form-control-label" for="message-participant">Participant</label>
            <select
              class="form-control"
              id="message-participant"
              data-cy="participant"
              name="participant"
              v-model="message.participant"
              required
            >
              <option v-if="!message.participant" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  message.participant && participantOption.id === message.participant.id ? message.participant : participantOption
                "
                v-for="participantOption in participants"
                :key="participantOption.id"
              >
                {{ participantOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.participant.$anyDirty && v$.participant.$invalid">
            <small class="form-text text-danger" v-for="error of v$.participant.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./message-update.component.ts"></script>
