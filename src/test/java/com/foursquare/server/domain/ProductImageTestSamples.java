package com.foursquare.server.domain;

import java.util.UUID;

public class ProductImageTestSamples {

    public static ProductImage getProductImageSample1() {
        return new ProductImage()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .imageUri("imageUri1")
            .altText("altText1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static ProductImage getProductImageSample2() {
        return new ProductImage()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .imageUri("imageUri2")
            .altText("altText2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static ProductImage getProductImageRandomSampleGenerator() {
        return new ProductImage()
            .id(UUID.randomUUID())
            .imageUri(UUID.randomUUID().toString())
            .altText(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
