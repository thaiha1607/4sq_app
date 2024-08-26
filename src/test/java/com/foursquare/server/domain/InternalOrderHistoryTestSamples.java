package com.foursquare.server.domain;

import java.util.UUID;

public class InternalOrderHistoryTestSamples {

    public static InternalOrderHistory getInternalOrderHistorySample1() {
        return new InternalOrderHistory()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .note("note1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static InternalOrderHistory getInternalOrderHistorySample2() {
        return new InternalOrderHistory()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .note("note2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static InternalOrderHistory getInternalOrderHistoryRandomSampleGenerator() {
        return new InternalOrderHistory()
            .id(UUID.randomUUID())
            .note(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
