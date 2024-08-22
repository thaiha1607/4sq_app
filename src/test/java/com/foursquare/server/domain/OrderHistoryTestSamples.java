package com.foursquare.server.domain;

import java.util.UUID;

public class OrderHistoryTestSamples {

    public static OrderHistory getOrderHistorySample1() {
        return new OrderHistory()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .comments("comments1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static OrderHistory getOrderHistorySample2() {
        return new OrderHistory()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .comments("comments2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static OrderHistory getOrderHistoryRandomSampleGenerator() {
        return new OrderHistory()
            .id(UUID.randomUUID())
            .comments(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
