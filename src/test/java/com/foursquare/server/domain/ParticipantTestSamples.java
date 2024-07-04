package com.foursquare.server.domain;

import java.util.UUID;

public class ParticipantTestSamples {

    public static Participant getParticipantSample1() {
        return new Participant()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Participant getParticipantSample2() {
        return new Participant()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Participant getParticipantRandomSampleGenerator() {
        return new Participant().id(UUID.randomUUID()).createdBy(UUID.randomUUID().toString()).lastModifiedBy(UUID.randomUUID().toString());
    }
}
