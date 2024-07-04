package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WorkingUnitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkingUnitDTO.class);
        WorkingUnitDTO workingUnitDTO1 = new WorkingUnitDTO();
        workingUnitDTO1.setId(UUID.randomUUID());
        WorkingUnitDTO workingUnitDTO2 = new WorkingUnitDTO();
        assertThat(workingUnitDTO1).isNotEqualTo(workingUnitDTO2);
        workingUnitDTO2.setId(workingUnitDTO1.getId());
        assertThat(workingUnitDTO1).isEqualTo(workingUnitDTO2);
        workingUnitDTO2.setId(UUID.randomUUID());
        assertThat(workingUnitDTO1).isNotEqualTo(workingUnitDTO2);
        workingUnitDTO1.setId(null);
        assertThat(workingUnitDTO1).isNotEqualTo(workingUnitDTO2);
    }
}
