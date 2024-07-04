package com.foursquare.server.domain;

import static com.foursquare.server.domain.ColourTestSamples.*;
import static com.foursquare.server.domain.ProductCategoryTestSamples.*;
import static com.foursquare.server.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductCategory.class);
        ProductCategory productCategory1 = getProductCategorySample1();
        ProductCategory productCategory2 = new ProductCategory();
        assertThat(productCategory1).isNotEqualTo(productCategory2);

        productCategory2.setId(productCategory1.getId());
        assertThat(productCategory1).isEqualTo(productCategory2);

        productCategory2 = getProductCategorySample2();
        assertThat(productCategory1).isNotEqualTo(productCategory2);
    }

    @Test
    void colourTest() {
        ProductCategory productCategory = getProductCategoryRandomSampleGenerator();
        Colour colourBack = getColourRandomSampleGenerator();

        productCategory.setColour(colourBack);
        assertThat(productCategory.getColour()).isEqualTo(colourBack);

        productCategory.colour(null);
        assertThat(productCategory.getColour()).isNull();
    }

    @Test
    void productTest() {
        ProductCategory productCategory = getProductCategoryRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productCategory.setProduct(productBack);
        assertThat(productCategory.getProduct()).isEqualTo(productBack);

        productCategory.product(null);
        assertThat(productCategory.getProduct()).isNull();
    }
}
