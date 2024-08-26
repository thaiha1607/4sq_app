package com.foursquare.server.domain;

import static com.foursquare.server.domain.InvoiceStatusTestSamples.*;
import static com.foursquare.server.domain.InvoiceTestSamples.*;
import static com.foursquare.server.domain.InvoiceTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static com.foursquare.server.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Invoice.class);
        Invoice invoice1 = getInvoiceSample1();
        Invoice invoice2 = new Invoice();
        assertThat(invoice1).isNotEqualTo(invoice2);

        invoice2.setId(invoice1.getId());
        assertThat(invoice1).isEqualTo(invoice2);

        invoice2 = getInvoiceSample2();
        assertThat(invoice1).isNotEqualTo(invoice2);
    }

    @Test
    void childInvoiceTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        invoice.addChildInvoice(invoiceBack);
        assertThat(invoice.getChildInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getRootInvoice()).isEqualTo(invoice);

        invoice.removeChildInvoice(invoiceBack);
        assertThat(invoice.getChildInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getRootInvoice()).isNull();

        invoice.childInvoices(new HashSet<>(Set.of(invoiceBack)));
        assertThat(invoice.getChildInvoices()).containsOnly(invoiceBack);
        assertThat(invoiceBack.getRootInvoice()).isEqualTo(invoice);

        invoice.setChildInvoices(new HashSet<>());
        assertThat(invoice.getChildInvoices()).doesNotContain(invoiceBack);
        assertThat(invoiceBack.getRootInvoice()).isNull();
    }

    @Test
    void shipmentTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        invoice.addShipment(shipmentBack);
        assertThat(invoice.getShipments()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getInvoice()).isEqualTo(invoice);

        invoice.removeShipment(shipmentBack);
        assertThat(invoice.getShipments()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getInvoice()).isNull();

        invoice.shipments(new HashSet<>(Set.of(shipmentBack)));
        assertThat(invoice.getShipments()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getInvoice()).isEqualTo(invoice);

        invoice.setShipments(new HashSet<>());
        assertThat(invoice.getShipments()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getInvoice()).isNull();
    }

    @Test
    void statusTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        InvoiceStatus invoiceStatusBack = getInvoiceStatusRandomSampleGenerator();

        invoice.setStatus(invoiceStatusBack);
        assertThat(invoice.getStatus()).isEqualTo(invoiceStatusBack);

        invoice.status(null);
        assertThat(invoice.getStatus()).isNull();
    }

    @Test
    void orderTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        invoice.setOrder(orderBack);
        assertThat(invoice.getOrder()).isEqualTo(orderBack);

        invoice.order(null);
        assertThat(invoice.getOrder()).isNull();
    }

    @Test
    void rootInvoiceTest() {
        Invoice invoice = getInvoiceRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        invoice.setRootInvoice(invoiceBack);
        assertThat(invoice.getRootInvoice()).isEqualTo(invoiceBack);

        invoice.rootInvoice(null);
        assertThat(invoice.getRootInvoice()).isNull();
    }
}
