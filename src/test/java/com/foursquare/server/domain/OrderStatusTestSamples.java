package com.foursquare.server.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrderStatusTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OrderStatus getOrderStatusSample1() {
        return new OrderStatus()
            .id(1L)
            .statusCode("statusCode1")
            .description("description1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static OrderStatus getOrderStatusSample2() {
        return new OrderStatus()
            .id(2L)
            .statusCode("statusCode2")
            .description("description2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static OrderStatus getOrderStatusRandomSampleGenerator() {
        return new OrderStatus()
            .id(longCount.incrementAndGet())
            .statusCode(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
