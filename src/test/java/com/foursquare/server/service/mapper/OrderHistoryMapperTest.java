package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.OrderHistoryAsserts.*;
import static com.foursquare.server.domain.OrderHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderHistoryMapperTest {

    private OrderHistoryMapper orderHistoryMapper;

    @BeforeEach
    void setUp() {
        orderHistoryMapper = new OrderHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrderHistorySample1();
        var actual = orderHistoryMapper.toEntity(orderHistoryMapper.toDto(expected));
        assertOrderHistoryAllPropertiesEquals(expected, actual);
    }
}
