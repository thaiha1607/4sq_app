package com.foursquare.server.service.criteria;

import com.foursquare.server.domain.enumeration.OrderType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.foursquare.server.domain.Order} entity. This class is used
 * in {@link com.foursquare.server.web.rest.OrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderCriteria implements Serializable, Criteria {

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

    private IntegerFilter priority;

    private StringFilter note;

    private StringFilter otherInfo;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private UUIDFilter invoiceId;

    private UUIDFilter orderItemId;

    private UUIDFilter childOrderId;

    private UUIDFilter internalOrderId;

    private UUIDFilter shipmentId;

    private UUIDFilter historyId;

    private LongFilter customerId;

    private LongFilter statusId;

    private UUIDFilter addressId;

    private UUIDFilter rootOrderId;

    private Boolean distinct;

    public OrderCriteria() {}

    public OrderCriteria(OrderCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.type = other.optionalType().map(OrderTypeFilter::copy).orElse(null);
        this.priority = other.optionalPriority().map(IntegerFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.otherInfo = other.optionalOtherInfo().map(StringFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.invoiceId = other.optionalInvoiceId().map(UUIDFilter::copy).orElse(null);
        this.orderItemId = other.optionalOrderItemId().map(UUIDFilter::copy).orElse(null);
        this.childOrderId = other.optionalChildOrderId().map(UUIDFilter::copy).orElse(null);
        this.internalOrderId = other.optionalInternalOrderId().map(UUIDFilter::copy).orElse(null);
        this.shipmentId = other.optionalShipmentId().map(UUIDFilter::copy).orElse(null);
        this.historyId = other.optionalHistoryId().map(UUIDFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.statusId = other.optionalStatusId().map(LongFilter::copy).orElse(null);
        this.addressId = other.optionalAddressId().map(UUIDFilter::copy).orElse(null);
        this.rootOrderId = other.optionalRootOrderId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrderCriteria copy() {
        return new OrderCriteria(this);
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

    public IntegerFilter getPriority() {
        return priority;
    }

    public Optional<IntegerFilter> optionalPriority() {
        return Optional.ofNullable(priority);
    }

    public IntegerFilter priority() {
        if (priority == null) {
            setPriority(new IntegerFilter());
        }
        return priority;
    }

    public void setPriority(IntegerFilter priority) {
        this.priority = priority;
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

    public UUIDFilter getInvoiceId() {
        return invoiceId;
    }

    public Optional<UUIDFilter> optionalInvoiceId() {
        return Optional.ofNullable(invoiceId);
    }

    public UUIDFilter invoiceId() {
        if (invoiceId == null) {
            setInvoiceId(new UUIDFilter());
        }
        return invoiceId;
    }

    public void setInvoiceId(UUIDFilter invoiceId) {
        this.invoiceId = invoiceId;
    }

    public UUIDFilter getOrderItemId() {
        return orderItemId;
    }

    public Optional<UUIDFilter> optionalOrderItemId() {
        return Optional.ofNullable(orderItemId);
    }

    public UUIDFilter orderItemId() {
        if (orderItemId == null) {
            setOrderItemId(new UUIDFilter());
        }
        return orderItemId;
    }

    public void setOrderItemId(UUIDFilter orderItemId) {
        this.orderItemId = orderItemId;
    }

    public UUIDFilter getChildOrderId() {
        return childOrderId;
    }

    public Optional<UUIDFilter> optionalChildOrderId() {
        return Optional.ofNullable(childOrderId);
    }

    public UUIDFilter childOrderId() {
        if (childOrderId == null) {
            setChildOrderId(new UUIDFilter());
        }
        return childOrderId;
    }

    public void setChildOrderId(UUIDFilter childOrderId) {
        this.childOrderId = childOrderId;
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

    public UUIDFilter getShipmentId() {
        return shipmentId;
    }

    public Optional<UUIDFilter> optionalShipmentId() {
        return Optional.ofNullable(shipmentId);
    }

    public UUIDFilter shipmentId() {
        if (shipmentId == null) {
            setShipmentId(new UUIDFilter());
        }
        return shipmentId;
    }

    public void setShipmentId(UUIDFilter shipmentId) {
        this.shipmentId = shipmentId;
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

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
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

    public UUIDFilter getAddressId() {
        return addressId;
    }

    public Optional<UUIDFilter> optionalAddressId() {
        return Optional.ofNullable(addressId);
    }

    public UUIDFilter addressId() {
        if (addressId == null) {
            setAddressId(new UUIDFilter());
        }
        return addressId;
    }

    public void setAddressId(UUIDFilter addressId) {
        this.addressId = addressId;
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
        final OrderCriteria that = (OrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(note, that.note) &&
            Objects.equals(otherInfo, that.otherInfo) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(invoiceId, that.invoiceId) &&
            Objects.equals(orderItemId, that.orderItemId) &&
            Objects.equals(childOrderId, that.childOrderId) &&
            Objects.equals(internalOrderId, that.internalOrderId) &&
            Objects.equals(shipmentId, that.shipmentId) &&
            Objects.equals(historyId, that.historyId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(addressId, that.addressId) &&
            Objects.equals(rootOrderId, that.rootOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            type,
            priority,
            note,
            otherInfo,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            invoiceId,
            orderItemId,
            childOrderId,
            internalOrderId,
            shipmentId,
            historyId,
            customerId,
            statusId,
            addressId,
            rootOrderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalPriority().map(f -> "priority=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalOtherInfo().map(f -> "otherInfo=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalInvoiceId().map(f -> "invoiceId=" + f + ", ").orElse("") +
            optionalOrderItemId().map(f -> "orderItemId=" + f + ", ").orElse("") +
            optionalChildOrderId().map(f -> "childOrderId=" + f + ", ").orElse("") +
            optionalInternalOrderId().map(f -> "internalOrderId=" + f + ", ").orElse("") +
            optionalShipmentId().map(f -> "shipmentId=" + f + ", ").orElse("") +
            optionalHistoryId().map(f -> "historyId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalStatusId().map(f -> "statusId=" + f + ", ").orElse("") +
            optionalAddressId().map(f -> "addressId=" + f + ", ").orElse("") +
            optionalRootOrderId().map(f -> "rootOrderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
