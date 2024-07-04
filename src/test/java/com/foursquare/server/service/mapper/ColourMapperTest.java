package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.ColourAsserts.*;
import static com.foursquare.server.domain.ColourTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColourMapperTest {

    private ColourMapper colourMapper;

    @BeforeEach
    void setUp() {
        colourMapper = new ColourMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getColourSample1();
        var actual = colourMapper.toEntity(colourMapper.toDto(expected));
        assertColourAllPropertiesEquals(expected, actual);
    }
}
