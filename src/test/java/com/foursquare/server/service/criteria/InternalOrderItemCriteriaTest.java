package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InternalOrderItemCriteriaTest {

    @Test
    void newInternalOrderItemCriteriaHasAllFiltersNullTest() {
        var internalOrderItemCriteria = new InternalOrderItemCriteria();
        assertThat(internalOrderItemCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void internalOrderItemCriteriaFluentMethodsCreatesFiltersTest() {
        var internalOrderItemCriteria = new InternalOrderItemCriteria();

        setAllFilters(internalOrderItemCriteria);

        assertThat(internalOrderItemCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void internalOrderItemCriteriaCopyCreatesNullFilterTest() {
        var internalOrderItemCriteria = new InternalOrderItemCriteria();
        var copy = internalOrderItemCriteria.copy();

        assertThat(internalOrderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(internalOrderItemCriteria)
        );
    }

    @Test
    void internalOrderItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var internalOrderItemCriteria = new InternalOrderItemCriteria();
        setAllFilters(internalOrderItemCriteria);

        var copy = internalOrderItemCriteria.copy();

        assertThat(internalOrderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(internalOrderItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var internalOrderItemCriteria = new InternalOrderItemCriteria();

        assertThat(internalOrderItemCriteria).hasToString("InternalOrderItemCriteria{}");
    }

    private static void setAllFilters(InternalOrderItemCriteria internalOrderItemCriteria) {
        internalOrderItemCriteria.id();
        internalOrderItemCriteria.qty();
        internalOrderItemCriteria.note();
        internalOrderItemCriteria.createdBy();
        internalOrderItemCriteria.createdDate();
        internalOrderItemCriteria.lastModifiedBy();
        internalOrderItemCriteria.lastModifiedDate();
        internalOrderItemCriteria.orderItemId();
        internalOrderItemCriteria.distinct();
    }

    private static Condition<InternalOrderItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQty()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getOrderItemId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InternalOrderItemCriteria> copyFiltersAre(
        InternalOrderItemCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQty(), copy.getQty()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getOrderItemId(), copy.getOrderItemId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
