package com.foursquare.server.domain;

import static com.foursquare.server.domain.CommentTestSamples.*;
import static com.foursquare.server.domain.ProductCategoryTestSamples.*;
import static com.foursquare.server.domain.ProductImageTestSamples.*;
import static com.foursquare.server.domain.ProductTestSamples.*;
import static com.foursquare.server.domain.TagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void productCategoryTest() {
        Product product = getProductRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        product.addProductCategory(productCategoryBack);
        assertThat(product.getProductCategories()).containsOnly(productCategoryBack);
        assertThat(productCategoryBack.getProduct()).isEqualTo(product);

        product.removeProductCategory(productCategoryBack);
        assertThat(product.getProductCategories()).doesNotContain(productCategoryBack);
        assertThat(productCategoryBack.getProduct()).isNull();

        product.productCategories(new HashSet<>(Set.of(productCategoryBack)));
        assertThat(product.getProductCategories()).containsOnly(productCategoryBack);
        assertThat(productCategoryBack.getProduct()).isEqualTo(product);

        product.setProductCategories(new HashSet<>());
        assertThat(product.getProductCategories()).doesNotContain(productCategoryBack);
        assertThat(productCategoryBack.getProduct()).isNull();
    }

    @Test
    void productImageTest() {
        Product product = getProductRandomSampleGenerator();
        ProductImage productImageBack = getProductImageRandomSampleGenerator();

        product.addProductImage(productImageBack);
        assertThat(product.getProductImages()).containsOnly(productImageBack);
        assertThat(productImageBack.getProduct()).isEqualTo(product);

        product.removeProductImage(productImageBack);
        assertThat(product.getProductImages()).doesNotContain(productImageBack);
        assertThat(productImageBack.getProduct()).isNull();

        product.productImages(new HashSet<>(Set.of(productImageBack)));
        assertThat(product.getProductImages()).containsOnly(productImageBack);
        assertThat(productImageBack.getProduct()).isEqualTo(product);

        product.setProductImages(new HashSet<>());
        assertThat(product.getProductImages()).doesNotContain(productImageBack);
        assertThat(productImageBack.getProduct()).isNull();
    }

    @Test
    void commentTest() {
        Product product = getProductRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        product.addComment(commentBack);
        assertThat(product.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getProduct()).isEqualTo(product);

        product.removeComment(commentBack);
        assertThat(product.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getProduct()).isNull();

        product.comments(new HashSet<>(Set.of(commentBack)));
        assertThat(product.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getProduct()).isEqualTo(product);

        product.setComments(new HashSet<>());
        assertThat(product.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getProduct()).isNull();
    }

    @Test
    void tagTest() {
        Product product = getProductRandomSampleGenerator();
        Tag tagBack = getTagRandomSampleGenerator();

        product.addTag(tagBack);
        assertThat(product.getTags()).containsOnly(tagBack);

        product.removeTag(tagBack);
        assertThat(product.getTags()).doesNotContain(tagBack);

        product.tags(new HashSet<>(Set.of(tagBack)));
        assertThat(product.getTags()).containsOnly(tagBack);

        product.setTags(new HashSet<>());
        assertThat(product.getTags()).doesNotContain(tagBack);
    }
}
