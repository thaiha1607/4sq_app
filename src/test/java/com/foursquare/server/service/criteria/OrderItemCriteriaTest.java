package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrderItemCriteriaTest {

    @Test
    void newOrderItemCriteriaHasAllFiltersNullTest() {
        var orderItemCriteria = new OrderItemCriteria();
        assertThat(orderItemCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void orderItemCriteriaFluentMethodsCreatesFiltersTest() {
        var orderItemCriteria = new OrderItemCriteria();

        setAllFilters(orderItemCriteria);

        assertThat(orderItemCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void orderItemCriteriaCopyCreatesNullFilterTest() {
        var orderItemCriteria = new OrderItemCriteria();
        var copy = orderItemCriteria.copy();

        assertThat(orderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(orderItemCriteria)
        );
    }

    @Test
    void orderItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var orderItemCriteria = new OrderItemCriteria();
        setAllFilters(orderItemCriteria);

        var copy = orderItemCriteria.copy();

        assertThat(orderItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(orderItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var orderItemCriteria = new OrderItemCriteria();

        assertThat(orderItemCriteria).hasToString("OrderItemCriteria{}");
    }

    private static void setAllFilters(OrderItemCriteria orderItemCriteria) {
        orderItemCriteria.id();
        orderItemCriteria.orderedQty();
        orderItemCriteria.receivedQty();
        orderItemCriteria.unitPrice();
        orderItemCriteria.note();
        orderItemCriteria.createdBy();
        orderItemCriteria.createdDate();
        orderItemCriteria.lastModifiedBy();
        orderItemCriteria.lastModifiedDate();
        orderItemCriteria.productCategoryId();
        orderItemCriteria.orderId();
        orderItemCriteria.distinct();
    }

    private static Condition<OrderItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOrderedQty()) &&
                condition.apply(criteria.getReceivedQty()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getProductCategoryId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrderItemCriteria> copyFiltersAre(OrderItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOrderedQty(), copy.getOrderedQty()) &&
                condition.apply(criteria.getReceivedQty(), copy.getReceivedQty()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getProductCategoryId(), copy.getProductCategoryId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
