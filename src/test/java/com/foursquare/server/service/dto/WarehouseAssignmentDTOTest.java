package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WarehouseAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseAssignmentDTO.class);
        WarehouseAssignmentDTO warehouseAssignmentDTO1 = new WarehouseAssignmentDTO();
        warehouseAssignmentDTO1.setId(UUID.randomUUID());
        WarehouseAssignmentDTO warehouseAssignmentDTO2 = new WarehouseAssignmentDTO();
        assertThat(warehouseAssignmentDTO1).isNotEqualTo(warehouseAssignmentDTO2);
        warehouseAssignmentDTO2.setId(warehouseAssignmentDTO1.getId());
        assertThat(warehouseAssignmentDTO1).isEqualTo(warehouseAssignmentDTO2);
        warehouseAssignmentDTO2.setId(UUID.randomUUID());
        assertThat(warehouseAssignmentDTO1).isNotEqualTo(warehouseAssignmentDTO2);
        warehouseAssignmentDTO1.setId(null);
        assertThat(warehouseAssignmentDTO1).isNotEqualTo(warehouseAssignmentDTO2);
    }
}
