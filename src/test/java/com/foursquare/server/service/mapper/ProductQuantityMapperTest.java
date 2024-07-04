package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.ProductQuantityAsserts.*;
import static com.foursquare.server.domain.ProductQuantityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductQuantityMapperTest {

    private ProductQuantityMapper productQuantityMapper;

    @BeforeEach
    void setUp() {
        productQuantityMapper = new ProductQuantityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductQuantitySample1();
        var actual = productQuantityMapper.toEntity(productQuantityMapper.toDto(expected));
        assertProductQuantityAllPropertiesEquals(expected, actual);
    }
}
