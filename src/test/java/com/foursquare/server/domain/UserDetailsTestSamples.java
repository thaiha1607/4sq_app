package com.foursquare.server.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserDetailsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserDetails getUserDetailsSample1() {
        return new UserDetails().id(1L).phone("phone1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static UserDetails getUserDetailsSample2() {
        return new UserDetails().id(2L).phone("phone2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static UserDetails getUserDetailsRandomSampleGenerator() {
        return new UserDetails()
            .id(longCount.incrementAndGet())
            .phone(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
