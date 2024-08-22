package com.foursquare.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductCategoryAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductCategoryAllPropertiesEquals(ProductCategory expected, ProductCategory actual) {
        assertProductCategoryAutoGeneratedPropertiesEquals(expected, actual);
        assertProductCategoryAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductCategoryAllUpdatablePropertiesEquals(ProductCategory expected, ProductCategory actual) {
        assertProductCategoryUpdatableFieldsEquals(expected, actual);
        assertProductCategoryUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductCategoryAutoGeneratedPropertiesEquals(ProductCategory expected, ProductCategory actual) {
        assertThat(expected)
            .as("Verify ProductCategory auto generated properties")
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
    public static void assertProductCategoryUpdatableFieldsEquals(ProductCategory expected, ProductCategory actual) {
        assertThat(expected)
            .as("Verify ProductCategory relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductCategoryUpdatableRelationshipsEquals(ProductCategory expected, ProductCategory actual) {
        assertThat(expected)
            .as("Verify ProductCategory relationships")
            .satisfies(e -> assertThat(e.getColour()).as("check colour").isEqualTo(actual.getColour()))
            .satisfies(e -> assertThat(e.getProduct()).as("check product").isEqualTo(actual.getProduct()));
    }
}
