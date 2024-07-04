package com.foursquare.server.domain;

import static com.foursquare.server.domain.OrderItemTestSamples.*;
import static com.foursquare.server.domain.ShipmentItemTestSamples.*;
import static com.foursquare.server.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentItem.class);
        ShipmentItem shipmentItem1 = getShipmentItemSample1();
        ShipmentItem shipmentItem2 = new ShipmentItem();
        assertThat(shipmentItem1).isNotEqualTo(shipmentItem2);

        shipmentItem2.setId(shipmentItem1.getId());
        assertThat(shipmentItem1).isEqualTo(shipmentItem2);

        shipmentItem2 = getShipmentItemSample2();
        assertThat(shipmentItem1).isNotEqualTo(shipmentItem2);
    }

    @Test
    void orderItemTest() {
        ShipmentItem shipmentItem = getShipmentItemRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        shipmentItem.setOrderItem(orderItemBack);
        assertThat(shipmentItem.getOrderItem()).isEqualTo(orderItemBack);

        shipmentItem.orderItem(null);
        assertThat(shipmentItem.getOrderItem()).isNull();
    }

    @Test
    void shipmentTest() {
        ShipmentItem shipmentItem = getShipmentItemRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        shipmentItem.setShipment(shipmentBack);
        assertThat(shipmentItem.getShipment()).isEqualTo(shipmentBack);

        shipmentItem.shipment(null);
        assertThat(shipmentItem.getShipment()).isNull();
    }
}
