package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ShipmentAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentAssignmentDTO.class);
        ShipmentAssignmentDTO shipmentAssignmentDTO1 = new ShipmentAssignmentDTO();
        shipmentAssignmentDTO1.setId(UUID.randomUUID());
        ShipmentAssignmentDTO shipmentAssignmentDTO2 = new ShipmentAssignmentDTO();
        assertThat(shipmentAssignmentDTO1).isNotEqualTo(shipmentAssignmentDTO2);
        shipmentAssignmentDTO2.setId(shipmentAssignmentDTO1.getId());
        assertThat(shipmentAssignmentDTO1).isEqualTo(shipmentAssignmentDTO2);
        shipmentAssignmentDTO2.setId(UUID.randomUUID());
        assertThat(shipmentAssignmentDTO1).isNotEqualTo(shipmentAssignmentDTO2);
        shipmentAssignmentDTO1.setId(null);
        assertThat(shipmentAssignmentDTO1).isNotEqualTo(shipmentAssignmentDTO2);
    }
}
