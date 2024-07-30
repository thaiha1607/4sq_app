package com.foursquare.server.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceStatusTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InvoiceStatus getInvoiceStatusSample1() {
        return new InvoiceStatus()
            .id(1L)
            .statusCode("statusCode1")
            .description("description1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static InvoiceStatus getInvoiceStatusSample2() {
        return new InvoiceStatus()
            .id(2L)
            .statusCode("statusCode2")
            .description("description2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static InvoiceStatus getInvoiceStatusRandomSampleGenerator() {
        return new InvoiceStatus()
            .id(longCount.incrementAndGet())
            .statusCode(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
