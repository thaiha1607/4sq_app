package com.foursquare.server.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShipmentStatusTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ShipmentStatus getShipmentStatusSample1() {
        return new ShipmentStatus().statusCode(1L).description("description1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static ShipmentStatus getShipmentStatusSample2() {
        return new ShipmentStatus().statusCode(2L).description("description2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static ShipmentStatus getShipmentStatusRandomSampleGenerator() {
        return new ShipmentStatus()
            .statusCode(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
