package com.foursquare.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foursquare.server.domain.enumeration.InvoiceType;
import com.foursquare.server.domain.enumeration.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Invoice.
 */
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@org.springframework.data.elasticsearch.annotations.Document(indexName = "invoice")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Invoice extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private InvoiceType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private PaymentMethod paymentMethod;

    @Column(name = "note")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String note;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "assignments", "items", "status", "order", "invoice" }, allowSetters = true)
    private Set<Shipment> shipments = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private InvoiceStatus status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "invoices", "orderItems", "childOrders", "shipments", "creator", "customer", "status", "address", "parentOrder" },
        allowSetters = true
    )
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Invoice id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Invoice totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public InvoiceType getType() {
        return this.type;
    }

    public Invoice type(InvoiceType type) {
        this.setType(type);
        return this;
    }

    public void setType(InvoiceType type) {
        this.type = type;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public Invoice paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return this.note;
    }

    public Invoice note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Inherited createdBy methods
    public Invoice createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Invoice createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Invoice lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Invoice lastModifiedDate(Instant lastModifiedDate) {
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

    public Invoice setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Shipment> getShipments() {
        return this.shipments;
    }

    public void setShipments(Set<Shipment> shipments) {
        if (this.shipments != null) {
            this.shipments.forEach(i -> i.setInvoice(null));
        }
        if (shipments != null) {
            shipments.forEach(i -> i.setInvoice(this));
        }
        this.shipments = shipments;
    }

    public Invoice shipments(Set<Shipment> shipments) {
        this.setShipments(shipments);
        return this;
    }

    public Invoice addShipment(Shipment shipment) {
        this.shipments.add(shipment);
        shipment.setInvoice(this);
        return this;
    }

    public Invoice removeShipment(Shipment shipment) {
        this.shipments.remove(shipment);
        shipment.setInvoice(null);
        return this;
    }

    public InvoiceStatus getStatus() {
        return this.status;
    }

    public void setStatus(InvoiceStatus invoiceStatus) {
        this.status = invoiceStatus;
    }

    public Invoice status(InvoiceStatus invoiceStatus) {
        this.setStatus(invoiceStatus);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Invoice order(Order order) {
        this.setOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return getId() != null && getId().equals(((Invoice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", totalAmount=" + getTotalAmount() +
            ", type='" + getType() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", note='" + getNote() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
