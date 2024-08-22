package com.foursquare.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A OrderItem.
 */
@Entity
@Table(name = "order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItem extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Min(value = 0)
    @Column(name = "ordered_qty", nullable = false)
    private Integer orderedQty;

    @NotNull
    @Min(value = 0)
    @Column(name = "received_qty", nullable = false)
    private Integer receivedQty;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "note")
    private String note;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @Transient
    private boolean isPersisted;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "colour", "product" }, allowSetters = true)
    private ProductCategory productCategory;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "invoices", "orderItems", "childOrders", "shipments", "histories", "customer", "status", "address", "parentOrder" },
        allowSetters = true
    )
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public OrderItem id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getOrderedQty() {
        return this.orderedQty;
    }

    public OrderItem orderedQty(Integer orderedQty) {
        this.setOrderedQty(orderedQty);
        return this;
    }

    public void setOrderedQty(Integer orderedQty) {
        this.orderedQty = orderedQty;
    }

    public Integer getReceivedQty() {
        return this.receivedQty;
    }

    public OrderItem receivedQty(Integer receivedQty) {
        this.setReceivedQty(receivedQty);
        return this;
    }

    public void setReceivedQty(Integer receivedQty) {
        this.receivedQty = receivedQty;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public OrderItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNote() {
        return this.note;
    }

    public OrderItem note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Inherited createdBy methods
    public OrderItem createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public OrderItem createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public OrderItem lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public OrderItem lastModifiedDate(Instant lastModifiedDate) {
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

    public OrderItem setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ProductCategory getProductCategory() {
        return this.productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public OrderItem productCategory(ProductCategory productCategory) {
        this.setProductCategory(productCategory);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem order(Order order) {
        this.setOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItem{" +
            "id=" + getId() +
            ", orderedQty=" + getOrderedQty() +
            ", receivedQty=" + getReceivedQty() +
            ", unitPrice=" + getUnitPrice() +
            ", note='" + getNote() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
