package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrderStatusCriteriaTest {

    @Test
    void newOrderStatusCriteriaHasAllFiltersNullTest() {
        var orderStatusCriteria = new OrderStatusCriteria();
        assertThat(orderStatusCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void orderStatusCriteriaFluentMethodsCreatesFiltersTest() {
        var orderStatusCriteria = new OrderStatusCriteria();

        setAllFilters(orderStatusCriteria);

        assertThat(orderStatusCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void orderStatusCriteriaCopyCreatesNullFilterTest() {
        var orderStatusCriteria = new OrderStatusCriteria();
        var copy = orderStatusCriteria.copy();

        assertThat(orderStatusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(orderStatusCriteria)
        );
    }

    @Test
    void orderStatusCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var orderStatusCriteria = new OrderStatusCriteria();
        setAllFilters(orderStatusCriteria);

        var copy = orderStatusCriteria.copy();

        assertThat(orderStatusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(orderStatusCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var orderStatusCriteria = new OrderStatusCriteria();

        assertThat(orderStatusCriteria).hasToString("OrderStatusCriteria{}");
    }

    private static void setAllFilters(OrderStatusCriteria orderStatusCriteria) {
        orderStatusCriteria.id();
        orderStatusCriteria.statusCode();
        orderStatusCriteria.description();
        orderStatusCriteria.createdBy();
        orderStatusCriteria.createdDate();
        orderStatusCriteria.lastModifiedBy();
        orderStatusCriteria.lastModifiedDate();
        orderStatusCriteria.distinct();
    }

    private static Condition<OrderStatusCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatusCode()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrderStatusCriteria> copyFiltersAre(OrderStatusCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatusCode(), copy.getStatusCode()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
