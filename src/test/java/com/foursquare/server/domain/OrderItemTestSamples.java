package com.foursquare.server.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OrderItem getOrderItemSample1() {
        return new OrderItem()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .orderedQty(1)
            .receivedQty(1)
            .note("note1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static OrderItem getOrderItemSample2() {
        return new OrderItem()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .orderedQty(2)
            .receivedQty(2)
            .note("note2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static OrderItem getOrderItemRandomSampleGenerator() {
        return new OrderItem()
            .id(UUID.randomUUID())
            .orderedQty(intCount.incrementAndGet())
            .receivedQty(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
