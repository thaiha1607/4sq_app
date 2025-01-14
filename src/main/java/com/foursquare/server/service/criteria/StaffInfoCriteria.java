package com.foursquare.server.service.criteria;

import com.foursquare.server.domain.enumeration.StaffRole;
import com.foursquare.server.domain.enumeration.StaffStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.foursquare.server.domain.StaffInfo} entity. This class is used
 * in {@link com.foursquare.server.web.rest.StaffInfoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /staff-infos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StaffInfoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StaffStatus
     */
    public static class StaffStatusFilter extends Filter<StaffStatus> {

        public StaffStatusFilter() {}

        public StaffStatusFilter(StaffStatusFilter filter) {
            super(filter);
        }

        @Override
        public StaffStatusFilter copy() {
            return new StaffStatusFilter(this);
        }
    }

    /**
     * Class for filtering StaffRole
     */
    public static class StaffRoleFilter extends Filter<StaffRole> {

        public StaffRoleFilter() {}

        public StaffRoleFilter(StaffRoleFilter filter) {
            super(filter);
        }

        @Override
        public StaffRoleFilter copy() {
            return new StaffRoleFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StaffStatusFilter status;

    private StaffRoleFilter role;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter userId;

    private UUIDFilter workingUnitId;

    private Boolean distinct;

    public StaffInfoCriteria() {}

    public StaffInfoCriteria(StaffInfoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StaffStatusFilter::copy).orElse(null);
        this.role = other.optionalRole().map(StaffRoleFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.workingUnitId = other.optionalWorkingUnitId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StaffInfoCriteria copy() {
        return new StaffInfoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StaffStatusFilter getStatus() {
        return status;
    }

    public Optional<StaffStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StaffStatusFilter status() {
        if (status == null) {
            setStatus(new StaffStatusFilter());
        }
        return status;
    }

    public void setStatus(StaffStatusFilter status) {
        this.status = status;
    }

    public StaffRoleFilter getRole() {
        return role;
    }

    public Optional<StaffRoleFilter> optionalRole() {
        return Optional.ofNullable(role);
    }

    public StaffRoleFilter role() {
        if (role == null) {
            setRole(new StaffRoleFilter());
        }
        return role;
    }

    public void setRole(StaffRoleFilter role) {
        this.role = role;
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

    public UUIDFilter getWorkingUnitId() {
        return workingUnitId;
    }

    public Optional<UUIDFilter> optionalWorkingUnitId() {
        return Optional.ofNullable(workingUnitId);
    }

    public UUIDFilter workingUnitId() {
        if (workingUnitId == null) {
            setWorkingUnitId(new UUIDFilter());
        }
        return workingUnitId;
    }

    public void setWorkingUnitId(UUIDFilter workingUnitId) {
        this.workingUnitId = workingUnitId;
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
        final StaffInfoCriteria that = (StaffInfoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(role, that.role) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(workingUnitId, that.workingUnitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate, userId, workingUnitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StaffInfoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalWorkingUnitId().map(f -> "workingUnitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
