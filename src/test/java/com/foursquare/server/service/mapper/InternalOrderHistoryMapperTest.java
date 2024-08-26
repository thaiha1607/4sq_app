package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.InternalOrderHistoryAsserts.*;
import static com.foursquare.server.domain.InternalOrderHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InternalOrderHistoryMapperTest {

    private InternalOrderHistoryMapper internalOrderHistoryMapper;

    @BeforeEach
    void setUp() {
        internalOrderHistoryMapper = new InternalOrderHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInternalOrderHistorySample1();
        var actual = internalOrderHistoryMapper.toEntity(internalOrderHistoryMapper.toDto(expected));
        assertInternalOrderHistoryAllPropertiesEquals(expected, actual);
    }
}
