package com.foursquare.server.domain;

import static com.foursquare.server.domain.InvoiceTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static com.foursquare.server.domain.ShipmentAssignmentTestSamples.*;
import static com.foursquare.server.domain.ShipmentItemTestSamples.*;
import static com.foursquare.server.domain.ShipmentStatusTestSamples.*;
import static com.foursquare.server.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shipment.class);
        Shipment shipment1 = getShipmentSample1();
        Shipment shipment2 = new Shipment();
        assertThat(shipment1).isNotEqualTo(shipment2);

        shipment2.setId(shipment1.getId());
        assertThat(shipment1).isEqualTo(shipment2);

        shipment2 = getShipmentSample2();
        assertThat(shipment1).isNotEqualTo(shipment2);
    }

    @Test
    void assignmentTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        ShipmentAssignment shipmentAssignmentBack = getShipmentAssignmentRandomSampleGenerator();

        shipment.addAssignment(shipmentAssignmentBack);
        assertThat(shipment.getAssignments()).containsOnly(shipmentAssignmentBack);
        assertThat(shipmentAssignmentBack.getShipment()).isEqualTo(shipment);

        shipment.removeAssignment(shipmentAssignmentBack);
        assertThat(shipment.getAssignments()).doesNotContain(shipmentAssignmentBack);
        assertThat(shipmentAssignmentBack.getShipment()).isNull();

        shipment.assignments(new HashSet<>(Set.of(shipmentAssignmentBack)));
        assertThat(shipment.getAssignments()).containsOnly(shipmentAssignmentBack);
        assertThat(shipmentAssignmentBack.getShipment()).isEqualTo(shipment);

        shipment.setAssignments(new HashSet<>());
        assertThat(shipment.getAssignments()).doesNotContain(shipmentAssignmentBack);
        assertThat(shipmentAssignmentBack.getShipment()).isNull();
    }

    @Test
    void itemTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        ShipmentItem shipmentItemBack = getShipmentItemRandomSampleGenerator();

        shipment.addItem(shipmentItemBack);
        assertThat(shipment.getItems()).containsOnly(shipmentItemBack);
        assertThat(shipmentItemBack.getShipment()).isEqualTo(shipment);

        shipment.removeItem(shipmentItemBack);
        assertThat(shipment.getItems()).doesNotContain(shipmentItemBack);
        assertThat(shipmentItemBack.getShipment()).isNull();

        shipment.items(new HashSet<>(Set.of(shipmentItemBack)));
        assertThat(shipment.getItems()).containsOnly(shipmentItemBack);
        assertThat(shipmentItemBack.getShipment()).isEqualTo(shipment);

        shipment.setItems(new HashSet<>());
        assertThat(shipment.getItems()).doesNotContain(shipmentItemBack);
        assertThat(shipmentItemBack.getShipment()).isNull();
    }

    @Test
    void statusTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        ShipmentStatus shipmentStatusBack = getShipmentStatusRandomSampleGenerator();

        shipment.setStatus(shipmentStatusBack);
        assertThat(shipment.getStatus()).isEqualTo(shipmentStatusBack);

        shipment.status(null);
        assertThat(shipment.getStatus()).isNull();
    }

    @Test
    void orderTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        shipment.setOrder(orderBack);
        assertThat(shipment.getOrder()).isEqualTo(orderBack);

        shipment.order(null);
        assertThat(shipment.getOrder()).isNull();
    }

    @Test
    void invoiceTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Invoice invoiceBack = getInvoiceRandomSampleGenerator();

        shipment.setInvoice(invoiceBack);
        assertThat(shipment.getInvoice()).isEqualTo(invoiceBack);

        shipment.invoice(null);
        assertThat(shipment.getInvoice()).isNull();
    }
}
