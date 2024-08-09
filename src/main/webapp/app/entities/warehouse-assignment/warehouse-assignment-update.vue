<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2 id="foursquareApp.warehouseAssignment.home.createOrEditLabel" data-cy="WarehouseAssignmentCreateUpdateHeading">
          Create or edit a Warehouse Assignment
        </h2>
        <div>
          <div class="form-group" v-if="warehouseAssignment.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="warehouseAssignment.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="warehouse-assignment-status"
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
            <label class="form-control-label" for="warehouse-assignment-note">Note</label>
            <input
              type="text"
              class="form-control"
              name="note"
              id="warehouse-assignment-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-otherInfo">Other Info</label>
            <input
              type="text"
              class="form-control"
              name="otherInfo"
              id="warehouse-assignment-otherInfo"
              data-cy="otherInfo"
              :class="{ valid: !v$.otherInfo.$invalid, invalid: v$.otherInfo.$invalid }"
              v-model="v$.otherInfo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="warehouse-assignment-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-createdDate">Created Date</label>
            <div class="d-flex">
              <input
                id="warehouse-assignment-createdDate"
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
            <label class="form-control-label" for="warehouse-assignment-lastModifiedBy">Last Modified By</label>
            <input
              type="text"
              class="form-control"
              name="lastModifiedBy"
              id="warehouse-assignment-lastModifiedBy"
              data-cy="lastModifiedBy"
              :class="{ valid: !v$.lastModifiedBy.$invalid, invalid: v$.lastModifiedBy.$invalid }"
              v-model="v$.lastModifiedBy.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-lastModifiedDate">Last Modified Date</label>
            <div class="d-flex">
              <input
                id="warehouse-assignment-lastModifiedDate"
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
            <label class="form-control-label" for="warehouse-assignment-user">User</label>
            <select class="form-control" id="warehouse-assignment-user" data-cy="user" name="user" v-model="warehouseAssignment.user">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  warehouseAssignment.user && userOption.id === warehouseAssignment.user.id ? warehouseAssignment.user : userOption
                "
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-sourceWorkingUnit">Source Working Unit</label>
            <select
              class="form-control"
              id="warehouse-assignment-sourceWorkingUnit"
              data-cy="sourceWorkingUnit"
              name="sourceWorkingUnit"
              v-model="warehouseAssignment.sourceWorkingUnit"
              required
            >
              <option v-if="!warehouseAssignment.sourceWorkingUnit" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  warehouseAssignment.sourceWorkingUnit && workingUnitOption.id === warehouseAssignment.sourceWorkingUnit.id
                    ? warehouseAssignment.sourceWorkingUnit
                    : workingUnitOption
                "
                v-for="workingUnitOption in workingUnits"
                :key="workingUnitOption.id"
              >
                {{ workingUnitOption.name }}
              </option>
            </select>
          </div>
          <div v-if="v$.sourceWorkingUnit.$anyDirty && v$.sourceWorkingUnit.$invalid">
            <small class="form-text text-danger" v-for="error of v$.sourceWorkingUnit.$errors" :key="error.$uid">{{
              error.$message
            }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-targetWorkingUnit">Target Working Unit</label>
            <select
              class="form-control"
              id="warehouse-assignment-targetWorkingUnit"
              data-cy="targetWorkingUnit"
              name="targetWorkingUnit"
              v-model="warehouseAssignment.targetWorkingUnit"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  warehouseAssignment.targetWorkingUnit && workingUnitOption.id === warehouseAssignment.targetWorkingUnit.id
                    ? warehouseAssignment.targetWorkingUnit
                    : workingUnitOption
                "
                v-for="workingUnitOption in workingUnits"
                :key="workingUnitOption.id"
              >
                {{ workingUnitOption.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="warehouse-assignment-order">Order</label>
            <select
              class="form-control"
              id="warehouse-assignment-order"
              data-cy="order"
              name="order"
              v-model="warehouseAssignment.order"
              required
            >
              <option v-if="!warehouseAssignment.order" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  warehouseAssignment.order && orderOption.id === warehouseAssignment.order.id ? warehouseAssignment.order : orderOption
                "
                v-for="orderOption in orders"
                :key="orderOption.id"
              >
                {{ orderOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.order.$anyDirty && v$.order.$invalid">
            <small class="form-text text-danger" v-for="error of v$.order.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./warehouse-assignment-update.component.ts"></script>
