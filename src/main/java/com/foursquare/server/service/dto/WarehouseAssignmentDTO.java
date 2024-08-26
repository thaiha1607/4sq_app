package com.foursquare.server.service.dto;

import com.foursquare.server.domain.enumeration.AssignmentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.foursquare.server.domain.WarehouseAssignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseAssignmentDTO implements Serializable {

    private UUID id;

    @NotNull
    private AssignmentStatus status;

    private String note;

    private String otherInfo;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private UserDTO user;

    @NotNull
    private WorkingUnitDTO sourceWorkingUnit;

    private WorkingUnitDTO targetWorkingUnit;

    @NotNull
    private InternalOrderDTO internalOrder;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public WorkingUnitDTO getSourceWorkingUnit() {
        return sourceWorkingUnit;
    }

    public void setSourceWorkingUnit(WorkingUnitDTO sourceWorkingUnit) {
        this.sourceWorkingUnit = sourceWorkingUnit;
    }

    public WorkingUnitDTO getTargetWorkingUnit() {
        return targetWorkingUnit;
    }

    public void setTargetWorkingUnit(WorkingUnitDTO targetWorkingUnit) {
        this.targetWorkingUnit = targetWorkingUnit;
    }

    public InternalOrderDTO getInternalOrder() {
        return internalOrder;
    }

    public void setInternalOrder(InternalOrderDTO internalOrder) {
        this.internalOrder = internalOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseAssignmentDTO)) {
            return false;
        }

        WarehouseAssignmentDTO warehouseAssignmentDTO = (WarehouseAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseAssignmentDTO{" +
            "id='" + getId() + "'" +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            ", otherInfo='" + getOtherInfo() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", user=" + getUser() +
            ", sourceWorkingUnit=" + getSourceWorkingUnit() +
            ", targetWorkingUnit=" + getTargetWorkingUnit() +
            ", internalOrder=" + getInternalOrder() +
            "}";
    }
}
