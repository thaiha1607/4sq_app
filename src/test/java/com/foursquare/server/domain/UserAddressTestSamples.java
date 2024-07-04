package com.foursquare.server.domain;

import java.util.UUID;

public class UserAddressTestSamples {

    public static UserAddress getUserAddressSample1() {
        return new UserAddress()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .friendlyName("friendlyName1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static UserAddress getUserAddressSample2() {
        return new UserAddress()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .friendlyName("friendlyName2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static UserAddress getUserAddressRandomSampleGenerator() {
        return new UserAddress()
            .id(UUID.randomUUID())
            .friendlyName(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
