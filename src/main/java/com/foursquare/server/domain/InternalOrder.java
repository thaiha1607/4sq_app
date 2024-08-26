package com.foursquare.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foursquare.server.domain.enumeration.OrderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A InternalOrder.
 */
@Entity
@Table(name = "internal_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InternalOrder extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrderType type;

    @Column(name = "note")
    private String note;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "status", "order" }, allowSetters = true)
    private Set<InternalOrderHistory> histories = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private OrderStatus status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "invoices",
            "orderItems",
            "childOrders",
            "internalOrders",
            "shipments",
            "histories",
            "customer",
            "status",
            "address",
            "rootOrder",
        },
        allowSetters = true
    )
    private Order rootOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public InternalOrder id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderType getType() {
        return this.type;
    }

    public InternalOrder type(OrderType type) {
        this.setType(type);
        return this;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public String getNote() {
        return this.note;
    }

    public InternalOrder note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Inherited createdBy methods
    public InternalOrder createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public InternalOrder createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public InternalOrder lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public InternalOrder lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public InternalOrder setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<InternalOrderHistory> getHistories() {
        return this.histories;
    }

    public void setHistories(Set<InternalOrderHistory> internalOrderHistories) {
        if (this.histories != null) {
            this.histories.forEach(i -> i.setOrder(null));
        }
        if (internalOrderHistories != null) {
            internalOrderHistories.forEach(i -> i.setOrder(this));
        }
        this.histories = internalOrderHistories;
    }

    public InternalOrder histories(Set<InternalOrderHistory> internalOrderHistories) {
        this.setHistories(internalOrderHistories);
        return this;
    }

    public InternalOrder addHistory(InternalOrderHistory internalOrderHistory) {
        this.histories.add(internalOrderHistory);
        internalOrderHistory.setOrder(this);
        return this;
    }

    public InternalOrder removeHistory(InternalOrderHistory internalOrderHistory) {
        this.histories.remove(internalOrderHistory);
        internalOrderHistory.setOrder(null);
        return this;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    public InternalOrder status(OrderStatus orderStatus) {
        this.setStatus(orderStatus);
        return this;
    }

    public Order getRootOrder() {
        return this.rootOrder;
    }

    public void setRootOrder(Order order) {
        this.rootOrder = order;
    }

    public InternalOrder rootOrder(Order order) {
        this.setRootOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((InternalOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InternalOrder{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", note='" + getNote() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
