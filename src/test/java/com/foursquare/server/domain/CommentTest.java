package com.foursquare.server.domain;

import static com.foursquare.server.domain.CommentTestSamples.*;
import static com.foursquare.server.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = getCommentSample1();
        Comment comment2 = new Comment();
        assertThat(comment1).isNotEqualTo(comment2);

        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);

        comment2 = getCommentSample2();
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void productTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        comment.setProduct(productBack);
        assertThat(comment.getProduct()).isEqualTo(productBack);

        comment.product(null);
        assertThat(comment.getProduct()).isNull();
    }
}
