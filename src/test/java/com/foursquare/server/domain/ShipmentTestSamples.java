package com.foursquare.server.domain;

import java.util.UUID;

public class ShipmentTestSamples {

    public static Shipment getShipmentSample1() {
        return new Shipment()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .note("note1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Shipment getShipmentSample2() {
        return new Shipment()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .note("note2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Shipment getShipmentRandomSampleGenerator() {
        return new Shipment()
            .id(UUID.randomUUID())
            .note(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
