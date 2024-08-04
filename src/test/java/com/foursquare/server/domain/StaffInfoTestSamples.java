package com.foursquare.server.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StaffInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StaffInfo getStaffInfoSample1() {
        return new StaffInfo().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static StaffInfo getStaffInfoSample2() {
        return new StaffInfo().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static StaffInfo getStaffInfoRandomSampleGenerator() {
        return new StaffInfo()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
