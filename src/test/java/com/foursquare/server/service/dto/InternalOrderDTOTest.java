package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InternalOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalOrderDTO.class);
        InternalOrderDTO internalOrderDTO1 = new InternalOrderDTO();
        internalOrderDTO1.setId(UUID.randomUUID());
        InternalOrderDTO internalOrderDTO2 = new InternalOrderDTO();
        assertThat(internalOrderDTO1).isNotEqualTo(internalOrderDTO2);
        internalOrderDTO2.setId(internalOrderDTO1.getId());
        assertThat(internalOrderDTO1).isEqualTo(internalOrderDTO2);
        internalOrderDTO2.setId(UUID.randomUUID());
        assertThat(internalOrderDTO1).isNotEqualTo(internalOrderDTO2);
        internalOrderDTO1.setId(null);
        assertThat(internalOrderDTO1).isNotEqualTo(internalOrderDTO2);
    }
}
