package com.foursquare.server.domain;

import java.util.UUID;

public class AddressTestSamples {

    public static Address getAddressSample1() {
        return new Address()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .line1("line11")
            .line2("line21")
            .city("city1")
            .state("state1")
            .country("country1")
            .zipOrPostalCode("zipOrPostalCode1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Address getAddressSample2() {
        return new Address()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .line1("line12")
            .line2("line22")
            .city("city2")
            .state("state2")
            .country("country2")
            .zipOrPostalCode("zipOrPostalCode2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address()
            .id(UUID.randomUUID())
            .line1(UUID.randomUUID().toString())
            .line2(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString())
            .zipOrPostalCode(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
