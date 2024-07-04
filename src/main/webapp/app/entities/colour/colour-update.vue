<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.colour.home.createOrEditLabel" data-cy="ColourCreateUpdateHeading">Create or edit a Colour</h2>
        <div>
          <div class="form-group" v-if="colour.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="colour.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="colour-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="colour-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="colour-hexCode">Hex Code</label>
            <input
              type="text"
              class="form-control"
              name="hexCode"
              id="colour-hexCode"
              data-cy="hexCode"
              :class="{ valid: !v$.hexCode.$invalid, invalid: v$.hexCode.$invalid }"
              v-model="v$.hexCode.$model"
              required
            />
            <div v-if="v$.hexCode.$anyDirty && v$.hexCode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.hexCode.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="colour-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="colour-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="colour-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="colour-createdDate"
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
            <label class="form-control-label" for="colour-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="colour-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="colour-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="colour-lastModifiedDate"
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
<script lang="ts" src="./colour-update.component.ts"></script>
