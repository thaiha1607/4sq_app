package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.InternalOrderAsserts.*;
import static com.foursquare.server.domain.InternalOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InternalOrderMapperTest {

    private InternalOrderMapper internalOrderMapper;

    @BeforeEach
    void setUp() {
        internalOrderMapper = new InternalOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInternalOrderSample1();
        var actual = internalOrderMapper.toEntity(internalOrderMapper.toDto(expected));
        assertInternalOrderAllPropertiesEquals(expected, actual);
    }
}
