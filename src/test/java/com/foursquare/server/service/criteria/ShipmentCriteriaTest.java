package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentCriteriaTest {

    @Test
    void newShipmentCriteriaHasAllFiltersNullTest() {
        var shipmentCriteria = new ShipmentCriteria();
        assertThat(shipmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void shipmentCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentCriteria = new ShipmentCriteria();

        setAllFilters(shipmentCriteria);

        assertThat(shipmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void shipmentCriteriaCopyCreatesNullFilterTest() {
        var shipmentCriteria = new ShipmentCriteria();
        var copy = shipmentCriteria.copy();

        assertThat(shipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentCriteria)
        );
    }

    @Test
    void shipmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentCriteria = new ShipmentCriteria();
        setAllFilters(shipmentCriteria);

        var copy = shipmentCriteria.copy();

        assertThat(shipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentCriteria = new ShipmentCriteria();

        assertThat(shipmentCriteria).hasToString("ShipmentCriteria{}");
    }

    private static void setAllFilters(ShipmentCriteria shipmentCriteria) {
        shipmentCriteria.id();
        shipmentCriteria.type();
        shipmentCriteria.shipmentDate();
        shipmentCriteria.note();
        shipmentCriteria.createdBy();
        shipmentCriteria.createdDate();
        shipmentCriteria.lastModifiedBy();
        shipmentCriteria.lastModifiedDate();
        shipmentCriteria.assignmentId();
        shipmentCriteria.itemId();
        shipmentCriteria.statusId();
        shipmentCriteria.orderId();
        shipmentCriteria.invoiceId();
        shipmentCriteria.distinct();
    }

    private static Condition<ShipmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getShipmentDate()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getAssignmentId()) &&
                condition.apply(criteria.getItemId()) &&
                condition.apply(criteria.getStatusId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getInvoiceId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShipmentCriteria> copyFiltersAre(ShipmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getShipmentDate(), copy.getShipmentDate()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getAssignmentId(), copy.getAssignmentId()) &&
                condition.apply(criteria.getItemId(), copy.getItemId()) &&
                condition.apply(criteria.getStatusId(), copy.getStatusId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getInvoiceId(), copy.getInvoiceId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
