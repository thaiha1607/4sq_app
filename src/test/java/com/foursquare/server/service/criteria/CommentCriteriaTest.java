package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CommentCriteriaTest {

    @Test
    void newCommentCriteriaHasAllFiltersNullTest() {
        var commentCriteria = new CommentCriteria();
        assertThat(commentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void commentCriteriaFluentMethodsCreatesFiltersTest() {
        var commentCriteria = new CommentCriteria();

        setAllFilters(commentCriteria);

        assertThat(commentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void commentCriteriaCopyCreatesNullFilterTest() {
        var commentCriteria = new CommentCriteria();
        var copy = commentCriteria.copy();

        assertThat(commentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(commentCriteria)
        );
    }

    @Test
    void commentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var commentCriteria = new CommentCriteria();
        setAllFilters(commentCriteria);

        var copy = commentCriteria.copy();

        assertThat(commentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(commentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var commentCriteria = new CommentCriteria();

        assertThat(commentCriteria).hasToString("CommentCriteria{}");
    }

    private static void setAllFilters(CommentCriteria commentCriteria) {
        commentCriteria.id();
        commentCriteria.rating();
        commentCriteria.content();
        commentCriteria.createdBy();
        commentCriteria.createdDate();
        commentCriteria.lastModifiedBy();
        commentCriteria.lastModifiedDate();
        commentCriteria.userId();
        commentCriteria.productId();
        commentCriteria.distinct();
    }

    private static Condition<CommentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRating()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CommentCriteria> copyFiltersAre(CommentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRating(), copy.getRating()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
