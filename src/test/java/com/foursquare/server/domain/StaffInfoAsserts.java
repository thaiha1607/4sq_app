package com.foursquare.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class StaffInfoAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStaffInfoAllPropertiesEquals(StaffInfo expected, StaffInfo actual) {
        assertStaffInfoAutoGeneratedPropertiesEquals(expected, actual);
        assertStaffInfoAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStaffInfoAllUpdatablePropertiesEquals(StaffInfo expected, StaffInfo actual) {
        assertStaffInfoUpdatableFieldsEquals(expected, actual);
        assertStaffInfoUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStaffInfoAutoGeneratedPropertiesEquals(StaffInfo expected, StaffInfo actual) {
        assertThat(expected)
            .as("Verify StaffInfo auto generated properties")
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
    public static void assertStaffInfoUpdatableFieldsEquals(StaffInfo expected, StaffInfo actual) {
        assertThat(expected)
            .as("Verify StaffInfo relevant properties")
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStaffInfoUpdatableRelationshipsEquals(StaffInfo expected, StaffInfo actual) {
        assertThat(expected)
            .as("Verify StaffInfo relationships")
            .satisfies(e -> assertThat(e.getWorkingUnit()).as("check workingUnit").isEqualTo(actual.getWorkingUnit()));
    }
}