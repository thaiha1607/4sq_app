package com.foursquare.server.service.criteria;

import com.foursquare.server.domain.enumeration.OrderType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.foursquare.server.domain.InternalOrder} entity. This class is used
 * in {@link com.foursquare.server.web.rest.InternalOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /internal-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InternalOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderType
     */
    public static class OrderTypeFilter extends Filter<OrderType> {

        public OrderTypeFilter() {}

        public OrderTypeFilter(OrderTypeFilter filter) {
            super(filter);
        }

        @Override
        public OrderTypeFilter copy() {
            return new OrderTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private OrderTypeFilter type;

    private StringFilter note;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private UUIDFilter historyId;

    private LongFilter statusId;

    private UUIDFilter rootOrderId;

    private Boolean distinct;

    public InternalOrderCriteria() {}

    public InternalOrderCriteria(InternalOrderCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.type = other.optionalType().map(OrderTypeFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.historyId = other.optionalHistoryId().map(UUIDFilter::copy).orElse(null);
        this.statusId = other.optionalStatusId().map(LongFilter::copy).orElse(null);
        this.rootOrderId = other.optionalRootOrderId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InternalOrderCriteria copy() {
        return new InternalOrderCriteria(this);
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

    public OrderTypeFilter getType() {
        return type;
    }

    public Optional<OrderTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public OrderTypeFilter type() {
        if (type == null) {
            setType(new OrderTypeFilter());
        }
        return type;
    }

    public void setType(OrderTypeFilter type) {
        this.type = type;
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

    public UUIDFilter getHistoryId() {
        return historyId;
    }

    public Optional<UUIDFilter> optionalHistoryId() {
        return Optional.ofNullable(historyId);
    }

    public UUIDFilter historyId() {
        if (historyId == null) {
            setHistoryId(new UUIDFilter());
        }
        return historyId;
    }

    public void setHistoryId(UUIDFilter historyId) {
        this.historyId = historyId;
    }

    public LongFilter getStatusId() {
        return statusId;
    }

    public Optional<LongFilter> optionalStatusId() {
        return Optional.ofNullable(statusId);
    }

    public LongFilter statusId() {
        if (statusId == null) {
            setStatusId(new LongFilter());
        }
        return statusId;
    }

    public void setStatusId(LongFilter statusId) {
        this.statusId = statusId;
    }

    public UUIDFilter getRootOrderId() {
        return rootOrderId;
    }

    public Optional<UUIDFilter> optionalRootOrderId() {
        return Optional.ofNullable(rootOrderId);
    }

    public UUIDFilter rootOrderId() {
        if (rootOrderId == null) {
            setRootOrderId(new UUIDFilter());
        }
        return rootOrderId;
    }

    public void setRootOrderId(UUIDFilter rootOrderId) {
        this.rootOrderId = rootOrderId;
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
        final InternalOrderCriteria that = (InternalOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(note, that.note) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(historyId, that.historyId) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(rootOrderId, that.rootOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            type,
            note,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            historyId,
            statusId,
            rootOrderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InternalOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalHistoryId().map(f -> "historyId=" + f + ", ").orElse("") +
            optionalStatusId().map(f -> "statusId=" + f + ", ").orElse("") +
            optionalRootOrderId().map(f -> "rootOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
