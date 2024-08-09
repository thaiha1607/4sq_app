<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.shipmentAssignment.home.createOrEditLabel" data-cy="ShipmentAssignmentCreateUpdateHeading">
          Create or edit a Shipment Assignment
        </h2>
        <div>
          <div class="form-group" v-if="shipmentAssignment.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shipmentAssignment.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="shipment-assignment-status"
              data-cy="status"
              required
            >
              <option v-for="assignmentStatus in assignmentStatusValues" :key="assignmentStatus" v-bind:value="assignmentStatus">
                {{ assignmentStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="shipment-assignment-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-otherInfo">Other Info</label>
            <input
              type="text"
              class="form-control"
              name="otherInfo"
              id="shipment-assignment-otherInfo"
              data-cy="otherInfo"
              :class="{ valid: !v$.otherInfo.$invalid, invalid: v$.otherInfo.$invalid }"
              v-model="v$.otherInfo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="shipment-assignment-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="shipment-assignment-createdDate"
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
            <label class="form-control-label" for="shipment-assignment-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="shipment-assignment-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="shipment-assignment-lastModifiedDate"
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
            <label class="form-control-label" for="shipment-assignment-user">User</label>
            <select class="form-control" id="shipment-assignment-user" data-cy="user" name="user" v-model="shipmentAssignment.user">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  shipmentAssignment.user && userOption.id === shipmentAssignment.user.id ? shipmentAssignment.user : userOption
                "
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="shipment-assignment-shipment">Shipment</label>
            <select
              class="form-control"
              id="shipment-assignment-shipment"
              data-cy="shipment"
              name="shipment"
              v-model="shipmentAssignment.shipment"
              required
            >
              <option v-if="!shipmentAssignment.shipment" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  shipmentAssignment.shipment && shipmentOption.id === shipmentAssignment.shipment.id
                    ? shipmentAssignment.shipment
                    : shipmentOption
                "
                v-for="shipmentOption in shipments"
                :key="shipmentOption.id"
              >
                {{ shipmentOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.shipment.$anyDirty && v$.shipment.$invalid">
            <small class="form-text text-danger" v-for="error of v$.shipment.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./shipment-assignment-update.component.ts"></script>
