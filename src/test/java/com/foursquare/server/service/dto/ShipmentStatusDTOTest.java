package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentStatusDTO.class);
        ShipmentStatusDTO shipmentStatusDTO1 = new ShipmentStatusDTO();
        shipmentStatusDTO1.setId(1L);
        ShipmentStatusDTO shipmentStatusDTO2 = new ShipmentStatusDTO();
        assertThat(shipmentStatusDTO1).isNotEqualTo(shipmentStatusDTO2);
        shipmentStatusDTO2.setId(shipmentStatusDTO1.getId());
        assertThat(shipmentStatusDTO1).isEqualTo(shipmentStatusDTO2);
        shipmentStatusDTO2.setId(2L);
        assertThat(shipmentStatusDTO1).isNotEqualTo(shipmentStatusDTO2);
        shipmentStatusDTO1.setId(null);
        assertThat(shipmentStatusDTO1).isNotEqualTo(shipmentStatusDTO2);
    }
}
