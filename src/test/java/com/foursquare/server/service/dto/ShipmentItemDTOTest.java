package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ShipmentItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentItemDTO.class);
        ShipmentItemDTO shipmentItemDTO1 = new ShipmentItemDTO();
        shipmentItemDTO1.setId(UUID.randomUUID());
        ShipmentItemDTO shipmentItemDTO2 = new ShipmentItemDTO();
        assertThat(shipmentItemDTO1).isNotEqualTo(shipmentItemDTO2);
        shipmentItemDTO2.setId(shipmentItemDTO1.getId());
        assertThat(shipmentItemDTO1).isEqualTo(shipmentItemDTO2);
        shipmentItemDTO2.setId(UUID.randomUUID());
        assertThat(shipmentItemDTO1).isNotEqualTo(shipmentItemDTO2);
        shipmentItemDTO1.setId(null);
        assertThat(shipmentItemDTO1).isNotEqualTo(shipmentItemDTO2);
    }
}
