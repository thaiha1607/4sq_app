package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InternalOrderHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalOrderHistoryDTO.class);
        InternalOrderHistoryDTO internalOrderHistoryDTO1 = new InternalOrderHistoryDTO();
        internalOrderHistoryDTO1.setId(UUID.randomUUID());
        InternalOrderHistoryDTO internalOrderHistoryDTO2 = new InternalOrderHistoryDTO();
        assertThat(internalOrderHistoryDTO1).isNotEqualTo(internalOrderHistoryDTO2);
        internalOrderHistoryDTO2.setId(internalOrderHistoryDTO1.getId());
        assertThat(internalOrderHistoryDTO1).isEqualTo(internalOrderHistoryDTO2);
        internalOrderHistoryDTO2.setId(UUID.randomUUID());
        assertThat(internalOrderHistoryDTO1).isNotEqualTo(internalOrderHistoryDTO2);
        internalOrderHistoryDTO1.setId(null);
        assertThat(internalOrderHistoryDTO1).isNotEqualTo(internalOrderHistoryDTO2);
    }
}
