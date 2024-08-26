package com.foursquare.server.service.criteria;

import com.foursquare.server.domain.enumeration.AssignmentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.foursquare.server.domain.WarehouseAssignment} entity. This class is used
 * in {@link com.foursquare.server.web.rest.WarehouseAssignmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /warehouse-assignments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseAssignmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AssignmentStatus
     */
    public static class AssignmentStatusFilter extends Filter<AssignmentStatus> {

        public AssignmentStatusFilter() {}

        public AssignmentStatusFilter(AssignmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public AssignmentStatusFilter copy() {
            return new AssignmentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private AssignmentStatusFilter status;

    private StringFilter note;

    private StringFilter otherInfo;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter userId;

    private UUIDFilter sourceWorkingUnitId;

    private UUIDFilter targetWorkingUnitId;

    private UUIDFilter internalOrderId;

    private Boolean distinct;

    public WarehouseAssignmentCriteria() {}

    public WarehouseAssignmentCriteria(WarehouseAssignmentCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AssignmentStatusFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.otherInfo = other.optionalOtherInfo().map(StringFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.sourceWorkingUnitId = other.optionalSourceWorkingUnitId().map(UUIDFilter::copy).orElse(null);
        this.targetWorkingUnitId = other.optionalTargetWorkingUnitId().map(UUIDFilter::copy).orElse(null);
        this.internalOrderId = other.optionalInternalOrderId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WarehouseAssignmentCriteria copy() {
        return new WarehouseAssignmentCriteria(this);
    }

    public UUIDFilter getId() {
        return id;
    }

    public Optional<UUIDFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public UUIDFilter id() {
        if (id == null) {
            setId(new UUIDFilter());
        }
        return id;
    }

    public void setId(UUIDFilter id) {
        this.id = id;
    }

    public AssignmentStatusFilter getStatus() {
        return status;
    }

    public Optional<AssignmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AssignmentStatusFilter status() {
        if (status == null) {
            setStatus(new AssignmentStatusFilter());
        }
        return status;
    }

    public void setStatus(AssignmentStatusFilter status) {
        this.status = status;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public StringFilter getOtherInfo() {
        return otherInfo;
    }

    public Optional<StringFilter> optionalOtherInfo() {
        return Optional.ofNullable(otherInfo);
    }

    public StringFilter otherInfo() {
        if (otherInfo == null) {
            setOtherInfo(new StringFilter());
        }
        return otherInfo;
    }

    public void setOtherInfo(StringFilter otherInfo) {
        this.otherInfo = otherInfo;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Optional<StringFilter> optionalLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            setLastModifiedBy(new StringFilter());
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public UUIDFilter getSourceWorkingUnitId() {
        return sourceWorkingUnitId;
    }

    public Optional<UUIDFilter> optionalSourceWorkingUnitId() {
        return Optional.ofNullable(sourceWorkingUnitId);
    }

    public UUIDFilter sourceWorkingUnitId() {
        if (sourceWorkingUnitId == null) {
            setSourceWorkingUnitId(new UUIDFilter());
        }
        return sourceWorkingUnitId;
    }

    public void setSourceWorkingUnitId(UUIDFilter sourceWorkingUnitId) {
        this.sourceWorkingUnitId = sourceWorkingUnitId;
    }

    public UUIDFilter getTargetWorkingUnitId() {
        return targetWorkingUnitId;
    }

    public Optional<UUIDFilter> optionalTargetWorkingUnitId() {
        return Optional.ofNullable(targetWorkingUnitId);
    }

    public UUIDFilter targetWorkingUnitId() {
        if (targetWorkingUnitId == null) {
            setTargetWorkingUnitId(new UUIDFilter());
        }
        return targetWorkingUnitId;
    }

    public void setTargetWorkingUnitId(UUIDFilter targetWorkingUnitId) {
        this.targetWorkingUnitId = targetWorkingUnitId;
    }

    public UUIDFilter getInternalOrderId() {
        return internalOrderId;
    }

    public Optional<UUIDFilter> optionalInternalOrderId() {
        return Optional.ofNullable(internalOrderId);
    }

    public UUIDFilter internalOrderId() {
        if (internalOrderId == null) {
            setInternalOrderId(new UUIDFilter());
        }
        return internalOrderId;
    }

    public void setInternalOrderId(UUIDFilter internalOrderId) {
        this.internalOrderId = internalOrderId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WarehouseAssignmentCriteria that = (WarehouseAssignmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(note, that.note) &&
            Objects.equals(otherInfo, that.otherInfo) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(sourceWorkingUnitId, that.sourceWorkingUnitId) &&
            Objects.equals(targetWorkingUnitId, that.targetWorkingUnitId) &&
            Objects.equals(internalOrderId, that.internalOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            status,
            note,
            otherInfo,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            userId,
            sourceWorkingUnitId,
            targetWorkingUnitId,
            internalOrderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseAssignmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalOtherInfo().map(f -> "otherInfo=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalSourceWorkingUnitId().map(f -> "sourceWorkingUnitId=" + f + ", ").orElse("") +
            optionalTargetWorkingUnitId().map(f -> "targetWorkingUnitId=" + f + ", ").orElse("") +
            optionalInternalOrderId().map(f -> "internalOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
