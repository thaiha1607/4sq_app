package com.foursquare.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foursquare.server.domain.enumeration.ShipmentType;
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
 * A Shipment.
 */
@Entity
@Table(name = "shipment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@org.springframework.data.elasticsearch.annotations.Document(indexName = "shipment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Shipment extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ShipmentType type;

    @NotNull
    @Column(name = "shipment_date", nullable = false)
    private Instant shipmentDate;

    @Column(name = "note")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String note;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "shipment" }, allowSetters = true)
    private Set<ShipmentAssignment> assignments = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "orderItem", "shipment" }, allowSetters = true)
    private Set<ShipmentItem> items = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private ShipmentStatus status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "invoices", "orderItems", "childOrders", "shipments", "creator", "customer", "status", "address", "parentOrder" },
        allowSetters = true
    )
    private Order order;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "shipments", "status", "order" }, allowSetters = true)
    private Invoice invoice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Shipment id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ShipmentType getType() {
        return this.type;
    }

    public Shipment type(ShipmentType type) {
        this.setType(type);
        return this;
    }

    public void setType(ShipmentType type) {
        this.type = type;
    }

    public Instant getShipmentDate() {
        return this.shipmentDate;
    }

    public Shipment shipmentDate(Instant shipmentDate) {
        this.setShipmentDate(shipmentDate);
        return this;
    }

    public void setShipmentDate(Instant shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getNote() {
        return this.note;
    }

    public Shipment note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Inherited createdBy methods
    public Shipment createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Shipment createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Shipment lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Shipment lastModifiedDate(Instant lastModifiedDate) {
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

    public Shipment setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<ShipmentAssignment> getAssignments() {
        return this.assignments;
    }

    public void setAssignments(Set<ShipmentAssignment> shipmentAssignments) {
        if (this.assignments != null) {
            this.assignments.forEach(i -> i.setShipment(null));
        }
        if (shipmentAssignments != null) {
            shipmentAssignments.forEach(i -> i.setShipment(this));
        }
        this.assignments = shipmentAssignments;
    }

    public Shipment assignments(Set<ShipmentAssignment> shipmentAssignments) {
        this.setAssignments(shipmentAssignments);
        return this;
    }

    public Shipment addAssignment(ShipmentAssignment shipmentAssignment) {
        this.assignments.add(shipmentAssignment);
        shipmentAssignment.setShipment(this);
        return this;
    }

    public Shipment removeAssignment(ShipmentAssignment shipmentAssignment) {
        this.assignments.remove(shipmentAssignment);
        shipmentAssignment.setShipment(null);
        return this;
    }

    public Set<ShipmentItem> getItems() {
        return this.items;
    }

    public void setItems(Set<ShipmentItem> shipmentItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setShipment(null));
        }
        if (shipmentItems != null) {
            shipmentItems.forEach(i -> i.setShipment(this));
        }
        this.items = shipmentItems;
    }

    public Shipment items(Set<ShipmentItem> shipmentItems) {
        this.setItems(shipmentItems);
        return this;
    }

    public Shipment addItem(ShipmentItem shipmentItem) {
        this.items.add(shipmentItem);
        shipmentItem.setShipment(this);
        return this;
    }

    public Shipment removeItem(ShipmentItem shipmentItem) {
        this.items.remove(shipmentItem);
        shipmentItem.setShipment(null);
        return this;
    }

    public ShipmentStatus getStatus() {
        return this.status;
    }

    public void setStatus(ShipmentStatus shipmentStatus) {
        this.status = shipmentStatus;
    }

    public Shipment status(ShipmentStatus shipmentStatus) {
        this.setStatus(shipmentStatus);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Shipment order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Shipment invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Shipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shipment{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", shipmentDate='" + getShipmentDate() + "'" +
            ", note='" + getNote() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
