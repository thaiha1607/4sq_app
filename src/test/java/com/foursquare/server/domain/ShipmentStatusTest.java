package com.foursquare.server.domain;

import static com.foursquare.server.domain.ShipmentStatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentStatus.class);
        ShipmentStatus shipmentStatus1 = getShipmentStatusSample1();
        ShipmentStatus shipmentStatus2 = new ShipmentStatus();
        assertThat(shipmentStatus1).isNotEqualTo(shipmentStatus2);

        shipmentStatus2.setStatusCode(shipmentStatus1.getStatusCode());
        assertThat(shipmentStatus1).isEqualTo(shipmentStatus2);

        shipmentStatus2 = getShipmentStatusSample2();
        assertThat(shipmentStatus1).isNotEqualTo(shipmentStatus2);
    }
}
