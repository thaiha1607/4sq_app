package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentStatusCriteriaTest {

    @Test
    void newShipmentStatusCriteriaHasAllFiltersNullTest() {
        var shipmentStatusCriteria = new ShipmentStatusCriteria();
        assertThat(shipmentStatusCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void shipmentStatusCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentStatusCriteria = new ShipmentStatusCriteria();

        setAllFilters(shipmentStatusCriteria);

        assertThat(shipmentStatusCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void shipmentStatusCriteriaCopyCreatesNullFilterTest() {
        var shipmentStatusCriteria = new ShipmentStatusCriteria();
        var copy = shipmentStatusCriteria.copy();

        assertThat(shipmentStatusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentStatusCriteria)
        );
    }

    @Test
    void shipmentStatusCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentStatusCriteria = new ShipmentStatusCriteria();
        setAllFilters(shipmentStatusCriteria);

        var copy = shipmentStatusCriteria.copy();

        assertThat(shipmentStatusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentStatusCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentStatusCriteria = new ShipmentStatusCriteria();

        assertThat(shipmentStatusCriteria).hasToString("ShipmentStatusCriteria{}");
    }

    private static void setAllFilters(ShipmentStatusCriteria shipmentStatusCriteria) {
        shipmentStatusCriteria.id();
        shipmentStatusCriteria.statusCode();
        shipmentStatusCriteria.description();
        shipmentStatusCriteria.createdBy();
        shipmentStatusCriteria.createdDate();
        shipmentStatusCriteria.lastModifiedBy();
        shipmentStatusCriteria.lastModifiedDate();
        shipmentStatusCriteria.distinct();
    }

    private static Condition<ShipmentStatusCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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

    private static Condition<ShipmentStatusCriteria> copyFiltersAre(
        ShipmentStatusCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
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
