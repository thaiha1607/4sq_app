package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentAssignmentCriteriaTest {

    @Test
    void newShipmentAssignmentCriteriaHasAllFiltersNullTest() {
        var shipmentAssignmentCriteria = new ShipmentAssignmentCriteria();
        assertThat(shipmentAssignmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void shipmentAssignmentCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentAssignmentCriteria = new ShipmentAssignmentCriteria();

        setAllFilters(shipmentAssignmentCriteria);

        assertThat(shipmentAssignmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void shipmentAssignmentCriteriaCopyCreatesNullFilterTest() {
        var shipmentAssignmentCriteria = new ShipmentAssignmentCriteria();
        var copy = shipmentAssignmentCriteria.copy();

        assertThat(shipmentAssignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentAssignmentCriteria)
        );
    }

    @Test
    void shipmentAssignmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentAssignmentCriteria = new ShipmentAssignmentCriteria();
        setAllFilters(shipmentAssignmentCriteria);

        var copy = shipmentAssignmentCriteria.copy();

        assertThat(shipmentAssignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentAssignmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentAssignmentCriteria = new ShipmentAssignmentCriteria();

        assertThat(shipmentAssignmentCriteria).hasToString("ShipmentAssignmentCriteria{}");
    }

    private static void setAllFilters(ShipmentAssignmentCriteria shipmentAssignmentCriteria) {
        shipmentAssignmentCriteria.id();
        shipmentAssignmentCriteria.status();
        shipmentAssignmentCriteria.note();
        shipmentAssignmentCriteria.otherInfo();
        shipmentAssignmentCriteria.createdBy();
        shipmentAssignmentCriteria.createdDate();
        shipmentAssignmentCriteria.lastModifiedBy();
        shipmentAssignmentCriteria.lastModifiedDate();
        shipmentAssignmentCriteria.userId();
        shipmentAssignmentCriteria.shipmentId();
        shipmentAssignmentCriteria.distinct();
    }

    private static Condition<ShipmentAssignmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getOtherInfo()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getShipmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShipmentAssignmentCriteria> copyFiltersAre(
        ShipmentAssignmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getOtherInfo(), copy.getOtherInfo()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getShipmentId(), copy.getShipmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
