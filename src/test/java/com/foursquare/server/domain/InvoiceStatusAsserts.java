package com.foursquare.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceStatusAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInvoiceStatusAllPropertiesEquals(InvoiceStatus expected, InvoiceStatus actual) {
        assertInvoiceStatusAutoGeneratedPropertiesEquals(expected, actual);
        assertInvoiceStatusAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInvoiceStatusAllUpdatablePropertiesEquals(InvoiceStatus expected, InvoiceStatus actual) {
        assertInvoiceStatusUpdatableFieldsEquals(expected, actual);
        assertInvoiceStatusUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInvoiceStatusAutoGeneratedPropertiesEquals(InvoiceStatus expected, InvoiceStatus actual) {
        assertThat(expected)
            .as("Verify InvoiceStatus auto generated properties")
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
    public static void assertInvoiceStatusUpdatableFieldsEquals(InvoiceStatus expected, InvoiceStatus actual) {
        assertThat(expected)
            .as("Verify InvoiceStatus relevant properties")
            .satisfies(e -> assertThat(e.getStatusCode()).as("check statusCode").isEqualTo(actual.getStatusCode()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInvoiceStatusUpdatableRelationshipsEquals(InvoiceStatus expected, InvoiceStatus actual) {}
}
