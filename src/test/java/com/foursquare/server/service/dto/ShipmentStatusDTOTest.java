package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentStatusDTO.class);
        ShipmentStatusDTO shipmentStatusDTO1 = new ShipmentStatusDTO();
        shipmentStatusDTO1.setStatusCode(1L);
        ShipmentStatusDTO shipmentStatusDTO2 = new ShipmentStatusDTO();
        assertThat(shipmentStatusDTO1).isNotEqualTo(shipmentStatusDTO2);
        shipmentStatusDTO2.setStatusCode(shipmentStatusDTO1.getStatusCode());
        assertThat(shipmentStatusDTO1).isEqualTo(shipmentStatusDTO2);
        shipmentStatusDTO2.setStatusCode(2L);
        assertThat(shipmentStatusDTO1).isNotEqualTo(shipmentStatusDTO2);
        shipmentStatusDTO1.setStatusCode(null);
        assertThat(shipmentStatusDTO1).isNotEqualTo(shipmentStatusDTO2);
    }
}
