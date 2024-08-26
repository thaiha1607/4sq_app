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
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Order extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrderType type;

    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "priority")
    private Integer priority;

    @Column(name = "note")
    private String note;

    @Column(name = "other_info")
    private String otherInfo;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "childInvoices", "shipments", "status", "order", "rootInvoice" }, allowSetters = true)
    private Set<Invoice> invoices = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "internalOrderItems", "productCategory", "order" }, allowSetters = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rootOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Order> childOrders = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rootOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "histories", "status", "rootOrder" }, allowSetters = true)
    private Set<InternalOrder> internalOrders = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "assignments", "items", "status", "order", "invoice" }, allowSetters = true)
    private Set<Shipment> shipments = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "status", "order" }, allowSetters = true)
    private Set<OrderHistory> histories = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private User customer;

    @ManyToOne(optional = false)
    @NotNull
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Order id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderType getType() {
        return this.type;
    }

    public Order type(OrderType type) {
        this.setType(type);
        return this;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public Order priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getNote() {
        return this.note;
    }

    public Order note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOtherInfo() {
        return this.otherInfo;
    }

    public Order otherInfo(String otherInfo) {
        this.setOtherInfo(otherInfo);
        return this;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    // Inherited createdBy methods
    public Order createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Order createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Order lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Order lastModifiedDate(Instant lastModifiedDate) {
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

    public Order setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Invoice> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<Invoice> invoices) {
        if (this.invoices != null) {
            this.invoices.forEach(i -> i.setOrder(null));
        }
        if (invoices != null) {
            invoices.forEach(i -> i.setOrder(this));
        }
        this.invoices = invoices;
    }

    public Order invoices(Set<Invoice> invoices) {
        this.setInvoices(invoices);
        return this;
    }

    public Order addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setOrder(this);
        return this;
    }

    public Order removeInvoice(Invoice invoice) {
        this.invoices.remove(invoice);
        invoice.setOrder(null);
        return this;
    }

    public Set<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setOrder(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setOrder(this));
        }
        this.orderItems = orderItems;
    }

    public Order orderItems(Set<OrderItem> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public Order addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
        return this;
    }

    public Order removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public Set<Order> getChildOrders() {
        return this.childOrders;
    }

    public void setChildOrders(Set<Order> orders) {
        if (this.childOrders != null) {
            this.childOrders.forEach(i -> i.setRootOrder(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setRootOrder(this));
        }
        this.childOrders = orders;
    }

    public Order childOrders(Set<Order> orders) {
        this.setChildOrders(orders);
        return this;
    }

    public Order addChildOrder(Order order) {
        this.childOrders.add(order);
        order.setRootOrder(this);
        return this;
    }

    public Order removeChildOrder(Order order) {
        this.childOrders.remove(order);
        order.setRootOrder(null);
        return this;
    }

    public Set<InternalOrder> getInternalOrders() {
        return this.internalOrders;
    }

    public void setInternalOrders(Set<InternalOrder> internalOrders) {
        if (this.internalOrders != null) {
            this.internalOrders.forEach(i -> i.setRootOrder(null));
        }
        if (internalOrders != null) {
            internalOrders.forEach(i -> i.setRootOrder(this));
        }
        this.internalOrders = internalOrders;
    }

    public Order internalOrders(Set<InternalOrder> internalOrders) {
        this.setInternalOrders(internalOrders);
        return this;
    }

    public Order addInternalOrder(InternalOrder internalOrder) {
        this.internalOrders.add(internalOrder);
        internalOrder.setRootOrder(this);
        return this;
    }

    public Order removeInternalOrder(InternalOrder internalOrder) {
        this.internalOrders.remove(internalOrder);
        internalOrder.setRootOrder(null);
        return this;
    }

    public Set<Shipment> getShipments() {
        return this.shipments;
    }

    public void setShipments(Set<Shipment> shipments) {
        if (this.shipments != null) {
            this.shipments.forEach(i -> i.setOrder(null));
        }
        if (shipments != null) {
            shipments.forEach(i -> i.setOrder(this));
        }
        this.shipments = shipments;
    }

    public Order shipments(Set<Shipment> shipments) {
        this.setShipments(shipments);
        return this;
    }

    public Order addShipment(Shipment shipment) {
        this.shipments.add(shipment);
        shipment.setOrder(this);
        return this;
    }

    public Order removeShipment(Shipment shipment) {
        this.shipments.remove(shipment);
        shipment.setOrder(null);
        return this;
    }

    public Set<OrderHistory> getHistories() {
        return this.histories;
    }

    public void setHistories(Set<OrderHistory> orderHistories) {
        if (this.histories != null) {
            this.histories.forEach(i -> i.setOrder(null));
        }
        if (orderHistories != null) {
            orderHistories.forEach(i -> i.setOrder(this));
        }
        this.histories = orderHistories;
    }

    public Order histories(Set<OrderHistory> orderHistories) {
        this.setHistories(orderHistories);
        return this;
    }

    public Order addHistory(OrderHistory orderHistory) {
        this.histories.add(orderHistory);
        orderHistory.setOrder(this);
        return this;
    }

    public Order removeHistory(OrderHistory orderHistory) {
        this.histories.remove(orderHistory);
        orderHistory.setOrder(null);
        return this;
    }

    public User getCustomer() {
        return this.customer;
    }

    public void setCustomer(User user) {
        this.customer = user;
    }

    public Order customer(User user) {
        this.setCustomer(user);
        return this;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    public Order status(OrderStatus orderStatus) {
        this.setStatus(orderStatus);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Order address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Order getRootOrder() {
        return this.rootOrder;
    }

    public void setRootOrder(Order order) {
        this.rootOrder = order;
    }

    public Order rootOrder(Order order) {
        this.setRootOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return getId() != null && getId().equals(((Order) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", priority=" + getPriority() +
            ", note='" + getNote() + "'" +
            ", otherInfo='" + getOtherInfo() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
