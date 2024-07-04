package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.ShipmentAssignmentAsserts.*;
import static com.foursquare.server.domain.ShipmentAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentAssignmentMapperTest {

    private ShipmentAssignmentMapper shipmentAssignmentMapper;

    @BeforeEach
    void setUp() {
        shipmentAssignmentMapper = new ShipmentAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShipmentAssignmentSample1();
        var actual = shipmentAssignmentMapper.toEntity(shipmentAssignmentMapper.toDto(expected));
        assertShipmentAssignmentAllPropertiesEquals(expected, actual);
    }
}
