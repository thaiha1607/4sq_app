package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.UserDetailsAsserts.*;
import static com.foursquare.server.domain.UserDetailsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDetailsMapperTest {

    private UserDetailsMapper userDetailsMapper;

    @BeforeEach
    void setUp() {
        userDetailsMapper = new UserDetailsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserDetailsSample1();
        var actual = userDetailsMapper.toEntity(userDetailsMapper.toDto(expected));
        assertUserDetailsAllPropertiesEquals(expected, actual);
    }
}
