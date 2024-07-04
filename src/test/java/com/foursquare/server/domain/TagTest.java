package com.foursquare.server.domain;

import static com.foursquare.server.domain.ProductTestSamples.*;
import static com.foursquare.server.domain.TagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tag.class);
        Tag tag1 = getTagSample1();
        Tag tag2 = new Tag();
        assertThat(tag1).isNotEqualTo(tag2);

        tag2.setId(tag1.getId());
        assertThat(tag1).isEqualTo(tag2);

        tag2 = getTagSample2();
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    void productTest() {
        Tag tag = getTagRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        tag.addProduct(productBack);
        assertThat(tag.getProducts()).containsOnly(productBack);
        assertThat(productBack.getTags()).containsOnly(tag);

        tag.removeProduct(productBack);
        assertThat(tag.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getTags()).doesNotContain(tag);

        tag.products(new HashSet<>(Set.of(productBack)));
        assertThat(tag.getProducts()).containsOnly(productBack);
        assertThat(productBack.getTags()).containsOnly(tag);

        tag.setProducts(new HashSet<>());
        assertThat(tag.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getTags()).doesNotContain(tag);
    }
}
