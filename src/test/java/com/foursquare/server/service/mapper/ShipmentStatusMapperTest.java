package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.ShipmentStatusAsserts.*;
import static com.foursquare.server.domain.ShipmentStatusTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentStatusMapperTest {

    private ShipmentStatusMapper shipmentStatusMapper;

    @BeforeEach
    void setUp() {
        shipmentStatusMapper = new ShipmentStatusMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShipmentStatusSample1();
        var actual = shipmentStatusMapper.toEntity(shipmentStatusMapper.toDto(expected));
        assertShipmentStatusAllPropertiesEquals(expected, actual);
    }
}
