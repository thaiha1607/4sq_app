package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InternalOrderHistoryCriteriaTest {

    @Test
    void newInternalOrderHistoryCriteriaHasAllFiltersNullTest() {
        var internalOrderHistoryCriteria = new InternalOrderHistoryCriteria();
        assertThat(internalOrderHistoryCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void internalOrderHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var internalOrderHistoryCriteria = new InternalOrderHistoryCriteria();

        setAllFilters(internalOrderHistoryCriteria);

        assertThat(internalOrderHistoryCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void internalOrderHistoryCriteriaCopyCreatesNullFilterTest() {
        var internalOrderHistoryCriteria = new InternalOrderHistoryCriteria();
        var copy = internalOrderHistoryCriteria.copy();

        assertThat(internalOrderHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(internalOrderHistoryCriteria)
        );
    }

    @Test
    void internalOrderHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var internalOrderHistoryCriteria = new InternalOrderHistoryCriteria();
        setAllFilters(internalOrderHistoryCriteria);

        var copy = internalOrderHistoryCriteria.copy();

        assertThat(internalOrderHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(internalOrderHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var internalOrderHistoryCriteria = new InternalOrderHistoryCriteria();

        assertThat(internalOrderHistoryCriteria).hasToString("InternalOrderHistoryCriteria{}");
    }

    private static void setAllFilters(InternalOrderHistoryCriteria internalOrderHistoryCriteria) {
        internalOrderHistoryCriteria.id();
        internalOrderHistoryCriteria.note();
        internalOrderHistoryCriteria.createdBy();
        internalOrderHistoryCriteria.createdDate();
        internalOrderHistoryCriteria.lastModifiedBy();
        internalOrderHistoryCriteria.lastModifiedDate();
        internalOrderHistoryCriteria.statusId();
        internalOrderHistoryCriteria.orderId();
        internalOrderHistoryCriteria.distinct();
    }

    private static Condition<InternalOrderHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InternalOrderHistoryCriteria> copyFiltersAre(
        InternalOrderHistoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
