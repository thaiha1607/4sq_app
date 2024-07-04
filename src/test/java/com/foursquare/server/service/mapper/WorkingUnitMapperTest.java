package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.WorkingUnitAsserts.*;
import static com.foursquare.server.domain.WorkingUnitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkingUnitMapperTest {

    private WorkingUnitMapper workingUnitMapper;

    @BeforeEach
    void setUp() {
        workingUnitMapper = new WorkingUnitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWorkingUnitSample1();
        var actual = workingUnitMapper.toEntity(workingUnitMapper.toDto(expected));
        assertWorkingUnitAllPropertiesEquals(expected, actual);
    }
}
