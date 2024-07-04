package com.foursquare.server.domain;

import static com.foursquare.server.domain.AddressTestSamples.*;
import static com.foursquare.server.domain.UserAddressTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserAddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAddress.class);
        UserAddress userAddress1 = getUserAddressSample1();
        UserAddress userAddress2 = new UserAddress();
        assertThat(userAddress1).isNotEqualTo(userAddress2);

        userAddress2.setId(userAddress1.getId());
        assertThat(userAddress1).isEqualTo(userAddress2);

        userAddress2 = getUserAddressSample2();
        assertThat(userAddress1).isNotEqualTo(userAddress2);
    }

    @Test
    void addressTest() {
        UserAddress userAddress = getUserAddressRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        userAddress.setAddress(addressBack);
        assertThat(userAddress.getAddress()).isEqualTo(addressBack);

        userAddress.address(null);
        assertThat(userAddress.getAddress()).isNull();
    }
}
