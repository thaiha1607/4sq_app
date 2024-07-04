package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.WarehouseAssignmentAsserts.*;
import static com.foursquare.server.domain.WarehouseAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseAssignmentMapperTest {

    private WarehouseAssignmentMapper warehouseAssignmentMapper;

    @BeforeEach
    void setUp() {
        warehouseAssignmentMapper = new WarehouseAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWarehouseAssignmentSample1();
        var actual = warehouseAssignmentMapper.toEntity(warehouseAssignmentMapper.toDto(expected));
        assertWarehouseAssignmentAllPropertiesEquals(expected, actual);
    }
}
