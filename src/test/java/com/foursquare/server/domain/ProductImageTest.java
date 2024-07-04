package com.foursquare.server.domain;

import static com.foursquare.server.domain.ProductImageTestSamples.*;
import static com.foursquare.server.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductImage.class);
        ProductImage productImage1 = getProductImageSample1();
        ProductImage productImage2 = new ProductImage();
        assertThat(productImage1).isNotEqualTo(productImage2);

        productImage2.setId(productImage1.getId());
        assertThat(productImage1).isEqualTo(productImage2);

        productImage2 = getProductImageSample2();
        assertThat(productImage1).isNotEqualTo(productImage2);
    }

    @Test
    void productTest() {
        ProductImage productImage = getProductImageRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productImage.setProduct(productBack);
        assertThat(productImage.getProduct()).isEqualTo(productBack);

        productImage.product(null);
        assertThat(productImage.getProduct()).isNull();
    }
}
