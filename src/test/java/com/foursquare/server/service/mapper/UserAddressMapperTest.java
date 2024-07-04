package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.UserAddressAsserts.*;
import static com.foursquare.server.domain.UserAddressTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAddressMapperTest {

    private UserAddressMapper userAddressMapper;

    @BeforeEach
    void setUp() {
        userAddressMapper = new UserAddressMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserAddressSample1();
        var actual = userAddressMapper.toEntity(userAddressMapper.toDto(expected));
        assertUserAddressAllPropertiesEquals(expected, actual);
    }
}
