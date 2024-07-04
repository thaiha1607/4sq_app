package com.foursquare.server.domain;

import static com.foursquare.server.domain.ShipmentAssignmentTestSamples.*;
import static com.foursquare.server.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentAssignment.class);
        ShipmentAssignment shipmentAssignment1 = getShipmentAssignmentSample1();
        ShipmentAssignment shipmentAssignment2 = new ShipmentAssignment();
        assertThat(shipmentAssignment1).isNotEqualTo(shipmentAssignment2);

        shipmentAssignment2.setId(shipmentAssignment1.getId());
        assertThat(shipmentAssignment1).isEqualTo(shipmentAssignment2);

        shipmentAssignment2 = getShipmentAssignmentSample2();
        assertThat(shipmentAssignment1).isNotEqualTo(shipmentAssignment2);
    }

    @Test
    void shipmentTest() {
        ShipmentAssignment shipmentAssignment = getShipmentAssignmentRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        shipmentAssignment.setShipment(shipmentBack);
        assertThat(shipmentAssignment.getShipment()).isEqualTo(shipmentBack);

        shipmentAssignment.shipment(null);
        assertThat(shipmentAssignment.getShipment()).isNull();
    }
}
