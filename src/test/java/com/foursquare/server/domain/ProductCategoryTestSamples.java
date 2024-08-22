package com.foursquare.server.domain;

import java.util.UUID;

public class ProductCategoryTestSamples {

    public static ProductCategory getProductCategorySample1() {
        return new ProductCategory()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static ProductCategory getProductCategorySample2() {
        return new ProductCategory()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static ProductCategory getProductCategoryRandomSampleGenerator() {
        return new ProductCategory()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
