package com.foursquare.server.service.mapper;

import static com.foursquare.server.domain.InvoiceStatusAsserts.*;
import static com.foursquare.server.domain.InvoiceStatusTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceStatusMapperTest {

    private InvoiceStatusMapper invoiceStatusMapper;

    @BeforeEach
    void setUp() {
        invoiceStatusMapper = new InvoiceStatusMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInvoiceStatusSample1();
        var actual = invoiceStatusMapper.toEntity(invoiceStatusMapper.toDto(expected));
        assertInvoiceStatusAllPropertiesEquals(expected, actual);
    }
}
