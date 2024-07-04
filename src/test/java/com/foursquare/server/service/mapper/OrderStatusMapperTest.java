package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.OrderStatusAsserts.*;
import static com.foursquare.server.domain.OrderStatusTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderStatusMapperTest {

    private OrderStatusMapper orderStatusMapper;

    @BeforeEach
    void setUp() {
        orderStatusMapper = new OrderStatusMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrderStatusSample1();
        var actual = orderStatusMapper.toEntity(orderStatusMapper.toDto(expected));
        assertOrderStatusAllPropertiesEquals(expected, actual);
    }
}
