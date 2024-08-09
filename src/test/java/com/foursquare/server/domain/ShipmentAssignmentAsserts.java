package com.foursquare.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ShipmentAssignmentAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShipmentAssignmentAllPropertiesEquals(ShipmentAssignment expected, ShipmentAssignment actual) {
        assertShipmentAssignmentAutoGeneratedPropertiesEquals(expected, actual);
        assertShipmentAssignmentAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShipmentAssignmentAllUpdatablePropertiesEquals(ShipmentAssignment expected, ShipmentAssignment actual) {
        assertShipmentAssignmentUpdatableFieldsEquals(expected, actual);
        assertShipmentAssignmentUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShipmentAssignmentAutoGeneratedPropertiesEquals(ShipmentAssignment expected, ShipmentAssignment actual) {
        assertThat(expected)
            .as("Verify ShipmentAssignment auto generated properties")
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
    public static void assertShipmentAssignmentUpdatableFieldsEquals(ShipmentAssignment expected, ShipmentAssignment actual) {
        assertThat(expected)
            .as("Verify ShipmentAssignment relevant properties")
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()))
            .satisfies(e -> assertThat(e.getNote()).as("check note").isEqualTo(actual.getNote()))
            .satisfies(e -> assertThat(e.getOtherInfo()).as("check otherInfo").isEqualTo(actual.getOtherInfo()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertShipmentAssignmentUpdatableRelationshipsEquals(ShipmentAssignment expected, ShipmentAssignment actual) {
        assertThat(expected)
            .as("Verify ShipmentAssignment relationships")
            .satisfies(e -> assertThat(e.getShipment()).as("check shipment").isEqualTo(actual.getShipment()));
    }
}
