package com.foursquare.server.domain;

import static com.foursquare.server.domain.AddressTestSamples.*;
import static com.foursquare.server.domain.InvoiceTestSamples.*;
import static com.foursquare.server.domain.OrderItemTestSamples.*;
import static com.foursquare.server.domain.OrderStatusTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static com.foursquare.server.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Order.class);
        Order order1 = getOrderSample1();
        Order order2 = new Order();
        assertThat(order1).isNotEqualTo(order2);

        order2.setId(order1.getId());
        assertThat(order1).isEqualTo(order2);

        order2 = getOrderSample2();
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void invoiceTest() {
        Order order = getOrderRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        order.addInvoice(invoiceBack);
        assertThat(order.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getOrder()).isEqualTo(order);

        order.removeInvoice(invoiceBack);
        assertThat(order.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getOrder()).isNull();

        order.invoices(new HashSet<>(Set.of(invoiceBack)));
        assertThat(order.getInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getOrder()).isEqualTo(order);

        order.setInvoices(new HashSet<>());
        assertThat(order.getInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getOrder()).isNull();
    }

    @Test
    void orderItemTest() {
        Order order = getOrderRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        order.addOrderItem(orderItemBack);
        assertThat(order.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(order);

        order.removeOrderItem(orderItemBack);
        assertThat(order.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();

        order.orderItems(new HashSet<>(Set.of(orderItemBack)));
        assertThat(order.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(order);

        order.setOrderItems(new HashSet<>());
        assertThat(order.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();
    }

    @Test
    void childOrderTest() {
        Order order = getOrderRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        order.addChildOrder(orderBack);
        assertThat(order.getChildOrders()).containsOnly(orderBack);
        assertThat(orderBack.getParentOrder()).isEqualTo(order);

        order.removeChildOrder(orderBack);
        assertThat(order.getChildOrders()).doesNotContain(orderBack);
        assertThat(orderBack.getParentOrder()).isNull();

        order.childOrders(new HashSet<>(Set.of(orderBack)));
        assertThat(order.getChildOrders()).containsOnly(orderBack);
        assertThat(orderBack.getParentOrder()).isEqualTo(order);

        order.setChildOrders(new HashSet<>());
        assertThat(order.getChildOrders()).doesNotContain(orderBack);
        assertThat(orderBack.getParentOrder()).isNull();
    }

    @Test
    void shipmentTest() {
        Order order = getOrderRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        order.addShipment(shipmentBack);
        assertThat(order.getShipments()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getOrder()).isEqualTo(order);

        order.removeShipment(shipmentBack);
        assertThat(order.getShipments()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getOrder()).isNull();

        order.shipments(new HashSet<>(Set.of(shipmentBack)));
        assertThat(order.getShipments()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getOrder()).isEqualTo(order);

        order.setShipments(new HashSet<>());
        assertThat(order.getShipments()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getOrder()).isNull();
    }

    @Test
    void statusTest() {
        Order order = getOrderRandomSampleGenerator();
        OrderStatus orderStatusBack = getOrderStatusRandomSampleGenerator();

        order.setStatus(orderStatusBack);
        assertThat(order.getStatus()).isEqualTo(orderStatusBack);

        order.status(null);
        assertThat(order.getStatus()).isNull();
    }

    @Test
    void addressTest() {
        Order order = getOrderRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        order.setAddress(addressBack);
        assertThat(order.getAddress()).isEqualTo(addressBack);

        order.address(null);
        assertThat(order.getAddress()).isNull();
    }

    @Test
    void parentOrderTest() {
        Order order = getOrderRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        order.setParentOrder(orderBack);
        assertThat(order.getParentOrder()).isEqualTo(orderBack);

        order.parentOrder(null);
        assertThat(order.getParentOrder()).isNull();
    }
}
