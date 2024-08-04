package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.StaffInfoAsserts.*;
import static com.foursquare.server.domain.StaffInfoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StaffInfoMapperTest {

    private StaffInfoMapper staffInfoMapper;

    @BeforeEach
    void setUp() {
        staffInfoMapper = new StaffInfoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStaffInfoSample1();
        var actual = staffInfoMapper.toEntity(staffInfoMapper.toDto(expected));
        assertStaffInfoAllPropertiesEquals(expected, actual);
    }
}
