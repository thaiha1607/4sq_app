package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InternalOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalOrderItemDTO.class);
        InternalOrderItemDTO internalOrderItemDTO1 = new InternalOrderItemDTO();
        internalOrderItemDTO1.setId(UUID.randomUUID());
        InternalOrderItemDTO internalOrderItemDTO2 = new InternalOrderItemDTO();
        assertThat(internalOrderItemDTO1).isNotEqualTo(internalOrderItemDTO2);
        internalOrderItemDTO2.setId(internalOrderItemDTO1.getId());
        assertThat(internalOrderItemDTO1).isEqualTo(internalOrderItemDTO2);
        internalOrderItemDTO2.setId(UUID.randomUUID());
        assertThat(internalOrderItemDTO1).isNotEqualTo(internalOrderItemDTO2);
        internalOrderItemDTO1.setId(null);
        assertThat(internalOrderItemDTO1).isNotEqualTo(internalOrderItemDTO2);
    }
}
