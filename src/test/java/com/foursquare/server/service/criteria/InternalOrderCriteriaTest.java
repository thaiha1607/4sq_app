package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InternalOrderCriteriaTest {

    @Test
    void newInternalOrderCriteriaHasAllFiltersNullTest() {
        var internalOrderCriteria = new InternalOrderCriteria();
        assertThat(internalOrderCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void internalOrderCriteriaFluentMethodsCreatesFiltersTest() {
        var internalOrderCriteria = new InternalOrderCriteria();

        setAllFilters(internalOrderCriteria);

        assertThat(internalOrderCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void internalOrderCriteriaCopyCreatesNullFilterTest() {
        var internalOrderCriteria = new InternalOrderCriteria();
        var copy = internalOrderCriteria.copy();

        assertThat(internalOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(internalOrderCriteria)
        );
    }

    @Test
    void internalOrderCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var internalOrderCriteria = new InternalOrderCriteria();
        setAllFilters(internalOrderCriteria);

        var copy = internalOrderCriteria.copy();

        assertThat(internalOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(internalOrderCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var internalOrderCriteria = new InternalOrderCriteria();

        assertThat(internalOrderCriteria).hasToString("InternalOrderCriteria{}");
    }

    private static void setAllFilters(InternalOrderCriteria internalOrderCriteria) {
        internalOrderCriteria.id();
        internalOrderCriteria.type();
        internalOrderCriteria.note();
        internalOrderCriteria.createdBy();
        internalOrderCriteria.createdDate();
        internalOrderCriteria.lastModifiedBy();
        internalOrderCriteria.lastModifiedDate();
        internalOrderCriteria.historyId();
        internalOrderCriteria.statusId();
        internalOrderCriteria.rootOrderId();
        internalOrderCriteria.distinct();
    }

    private static Condition<InternalOrderCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getHistoryId()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getRootOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InternalOrderCriteria> copyFiltersAre(
        InternalOrderCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getHistoryId(), copy.getHistoryId()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getRootOrderId(), copy.getRootOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
