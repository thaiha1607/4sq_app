package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrderHistoryCriteriaTest {

    @Test
    void newOrderHistoryCriteriaHasAllFiltersNullTest() {
        var orderHistoryCriteria = new OrderHistoryCriteria();
        assertThat(orderHistoryCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void orderHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var orderHistoryCriteria = new OrderHistoryCriteria();

        setAllFilters(orderHistoryCriteria);

        assertThat(orderHistoryCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void orderHistoryCriteriaCopyCreatesNullFilterTest() {
        var orderHistoryCriteria = new OrderHistoryCriteria();
        var copy = orderHistoryCriteria.copy();

        assertThat(orderHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(orderHistoryCriteria)
        );
    }

    @Test
    void orderHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var orderHistoryCriteria = new OrderHistoryCriteria();
        setAllFilters(orderHistoryCriteria);

        var copy = orderHistoryCriteria.copy();

        assertThat(orderHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(orderHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var orderHistoryCriteria = new OrderHistoryCriteria();

        assertThat(orderHistoryCriteria).hasToString("OrderHistoryCriteria{}");
    }

    private static void setAllFilters(OrderHistoryCriteria orderHistoryCriteria) {
        orderHistoryCriteria.id();
        orderHistoryCriteria.note();
        orderHistoryCriteria.createdBy();
        orderHistoryCriteria.createdDate();
        orderHistoryCriteria.lastModifiedBy();
        orderHistoryCriteria.lastModifiedDate();
        orderHistoryCriteria.statusId();
        orderHistoryCriteria.orderId();
        orderHistoryCriteria.distinct();
    }

    private static Condition<OrderHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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

    private static Condition<OrderHistoryCriteria> copyFiltersAre(
        OrderHistoryCriteria copy,
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
