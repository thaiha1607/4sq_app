package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.ShipmentItemAsserts.*;
import static com.foursquare.server.domain.ShipmentItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentItemMapperTest {

    private ShipmentItemMapper shipmentItemMapper;

    @BeforeEach
    void setUp() {
        shipmentItemMapper = new ShipmentItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShipmentItemSample1();
        var actual = shipmentItemMapper.toEntity(shipmentItemMapper.toDto(expected));
        assertShipmentItemAllPropertiesEquals(expected, actual);
    }
}
