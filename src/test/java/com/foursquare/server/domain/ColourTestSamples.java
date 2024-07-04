package com.foursquare.server.domain;

import java.util.UUID;

public class ColourTestSamples {

    public static Colour getColourSample1() {
        return new Colour()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .hexCode("hexCode1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Colour getColourSample2() {
        return new Colour()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .hexCode("hexCode2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Colour getColourRandomSampleGenerator() {
        return new Colour()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .hexCode(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
