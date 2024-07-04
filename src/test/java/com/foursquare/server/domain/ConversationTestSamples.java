package com.foursquare.server.domain;

import java.util.UUID;

public class ConversationTestSamples {

    public static Conversation getConversationSample1() {
        return new Conversation()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .title("title1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Conversation getConversationSample2() {
        return new Conversation()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .title("title2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Conversation getConversationRandomSampleGenerator() {
        return new Conversation()
            .id(UUID.randomUUID())
            .title(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
