package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StaffInfoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StaffInfoDTO.class);
        StaffInfoDTO staffInfoDTO1 = new StaffInfoDTO();
        staffInfoDTO1.setId(1L);
        StaffInfoDTO staffInfoDTO2 = new StaffInfoDTO();
        assertThat(staffInfoDTO1).isNotEqualTo(staffInfoDTO2);
        staffInfoDTO2.setId(staffInfoDTO1.getId());
        assertThat(staffInfoDTO1).isEqualTo(staffInfoDTO2);
        staffInfoDTO2.setId(2L);
        assertThat(staffInfoDTO1).isNotEqualTo(staffInfoDTO2);
        staffInfoDTO1.setId(null);
        assertThat(staffInfoDTO1).isNotEqualTo(staffInfoDTO2);
    }
}
