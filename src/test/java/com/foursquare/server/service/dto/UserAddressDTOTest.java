package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserAddressDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAddressDTO.class);
        UserAddressDTO userAddressDTO1 = new UserAddressDTO();
        userAddressDTO1.setId(UUID.randomUUID());
        UserAddressDTO userAddressDTO2 = new UserAddressDTO();
        assertThat(userAddressDTO1).isNotEqualTo(userAddressDTO2);
        userAddressDTO2.setId(userAddressDTO1.getId());
        assertThat(userAddressDTO1).isEqualTo(userAddressDTO2);
        userAddressDTO2.setId(UUID.randomUUID());
        assertThat(userAddressDTO1).isNotEqualTo(userAddressDTO2);
        userAddressDTO1.setId(null);
        assertThat(userAddressDTO1).isNotEqualTo(userAddressDTO2);
    }
}
