package com.foursquare.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMessageAllPropertiesEquals(Message expected, Message actual) {
        assertMessageAutoGeneratedPropertiesEquals(expected, actual);
        assertMessageAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMessageAllUpdatablePropertiesEquals(Message expected, Message actual) {
        assertMessageUpdatableFieldsEquals(expected, actual);
        assertMessageUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMessageAutoGeneratedPropertiesEquals(Message expected, Message actual) {
        assertThat(expected)
            .as("Verify Message auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()))
            .satisfies(e -> assertThat(e.getCreatedBy()).as("check createdBy").isEqualTo(actual.getCreatedBy()))
            .satisfies(e -> assertThat(e.getCreatedDate()).as("check createdDate").isEqualTo(actual.getCreatedDate()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMessageUpdatableFieldsEquals(Message expected, Message actual) {
        assertThat(expected)
            .as("Verify Message relevant properties")
            .satisfies(e -> assertThat(e.getType()).as("check type").isEqualTo(actual.getType()))
            .satisfies(e -> assertThat(e.getContent()).as("check content").isEqualTo(actual.getContent()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMessageUpdatableRelationshipsEquals(Message expected, Message actual) {
        assertThat(expected)
            .as("Verify Message relationships")
            .satisfies(e -> assertThat(e.getParticipant()).as("check participant").isEqualTo(actual.getParticipant()))
            .satisfies(e -> assertThat(e.getSeenParticipants()).as("check seenParticipants").isEqualTo(actual.getSeenParticipants()));
    }
}
