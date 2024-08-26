package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.InternalOrderItemAsserts.*;
import static com.foursquare.server.domain.InternalOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InternalOrderItemMapperTest {

    private InternalOrderItemMapper internalOrderItemMapper;

    @BeforeEach
    void setUp() {
        internalOrderItemMapper = new InternalOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInternalOrderItemSample1();
        var actual = internalOrderItemMapper.toEntity(internalOrderItemMapper.toDto(expected));
        assertInternalOrderItemAllPropertiesEquals(expected, actual);
    }
}
