package com.foursquare.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressAllPropertiesEquals(Address expected, Address actual) {
        assertAddressAutoGeneratedPropertiesEquals(expected, actual);
        assertAddressAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressAllUpdatablePropertiesEquals(Address expected, Address actual) {
        assertAddressUpdatableFieldsEquals(expected, actual);
        assertAddressUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressAutoGeneratedPropertiesEquals(Address expected, Address actual) {
        assertThat(expected)
            .as("Verify Address auto generated properties")
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
    public static void assertAddressUpdatableFieldsEquals(Address expected, Address actual) {
        assertThat(expected)
            .as("Verify Address relevant properties")
            .satisfies(e -> assertThat(e.getLine1()).as("check line1").isEqualTo(actual.getLine1()))
            .satisfies(e -> assertThat(e.getLine2()).as("check line2").isEqualTo(actual.getLine2()))
            .satisfies(e -> assertThat(e.getCity()).as("check city").isEqualTo(actual.getCity()))
            .satisfies(e -> assertThat(e.getState()).as("check state").isEqualTo(actual.getState()))
            .satisfies(e -> assertThat(e.getCountry()).as("check country").isEqualTo(actual.getCountry()))
            .satisfies(e -> assertThat(e.getZipOrPostalCode()).as("check zipOrPostalCode").isEqualTo(actual.getZipOrPostalCode()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressUpdatableRelationshipsEquals(Address expected, Address actual) {}
}
