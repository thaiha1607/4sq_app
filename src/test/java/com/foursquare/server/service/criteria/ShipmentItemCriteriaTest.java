package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentItemCriteriaTest {

    @Test
    void newShipmentItemCriteriaHasAllFiltersNullTest() {
        var shipmentItemCriteria = new ShipmentItemCriteria();
        assertThat(shipmentItemCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void shipmentItemCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentItemCriteria = new ShipmentItemCriteria();

        setAllFilters(shipmentItemCriteria);

        assertThat(shipmentItemCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void shipmentItemCriteriaCopyCreatesNullFilterTest() {
        var shipmentItemCriteria = new ShipmentItemCriteria();
        var copy = shipmentItemCriteria.copy();

        assertThat(shipmentItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentItemCriteria)
        );
    }

    @Test
    void shipmentItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentItemCriteria = new ShipmentItemCriteria();
        setAllFilters(shipmentItemCriteria);

        var copy = shipmentItemCriteria.copy();

        assertThat(shipmentItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentItemCriteria = new ShipmentItemCriteria();

        assertThat(shipmentItemCriteria).hasToString("ShipmentItemCriteria{}");
    }

    private static void setAllFilters(ShipmentItemCriteria shipmentItemCriteria) {
        shipmentItemCriteria.id();
        shipmentItemCriteria.qty();
        shipmentItemCriteria.total();
        shipmentItemCriteria.rollQty();
        shipmentItemCriteria.createdBy();
        shipmentItemCriteria.createdDate();
        shipmentItemCriteria.lastModifiedBy();
        shipmentItemCriteria.lastModifiedDate();
        shipmentItemCriteria.orderItemId();
        shipmentItemCriteria.shipmentId();
        shipmentItemCriteria.distinct();
    }

    private static Condition<ShipmentItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQty()) &&
                condition.apply(criteria.getTotal()) &&
                condition.apply(criteria.getRollQty()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getOrderItemId()) &&
                condition.apply(criteria.getShipmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShipmentItemCriteria> copyFiltersAre(
        ShipmentItemCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQty(), copy.getQty()) &&
                condition.apply(criteria.getTotal(), copy.getTotal()) &&
                condition.apply(criteria.getRollQty(), copy.getRollQty()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getOrderItemId(), copy.getOrderItemId()) &&
                condition.apply(criteria.getShipmentId(), copy.getShipmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
