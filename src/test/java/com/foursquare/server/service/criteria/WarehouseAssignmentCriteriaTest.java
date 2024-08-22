package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WarehouseAssignmentCriteriaTest {

    @Test
    void newWarehouseAssignmentCriteriaHasAllFiltersNullTest() {
        var warehouseAssignmentCriteria = new WarehouseAssignmentCriteria();
        assertThat(warehouseAssignmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void warehouseAssignmentCriteriaFluentMethodsCreatesFiltersTest() {
        var warehouseAssignmentCriteria = new WarehouseAssignmentCriteria();

        setAllFilters(warehouseAssignmentCriteria);

        assertThat(warehouseAssignmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void warehouseAssignmentCriteriaCopyCreatesNullFilterTest() {
        var warehouseAssignmentCriteria = new WarehouseAssignmentCriteria();
        var copy = warehouseAssignmentCriteria.copy();

        assertThat(warehouseAssignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(warehouseAssignmentCriteria)
        );
    }

    @Test
    void warehouseAssignmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var warehouseAssignmentCriteria = new WarehouseAssignmentCriteria();
        setAllFilters(warehouseAssignmentCriteria);

        var copy = warehouseAssignmentCriteria.copy();

        assertThat(warehouseAssignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(warehouseAssignmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var warehouseAssignmentCriteria = new WarehouseAssignmentCriteria();

        assertThat(warehouseAssignmentCriteria).hasToString("WarehouseAssignmentCriteria{}");
    }

    private static void setAllFilters(WarehouseAssignmentCriteria warehouseAssignmentCriteria) {
        warehouseAssignmentCriteria.id();
        warehouseAssignmentCriteria.status();
        warehouseAssignmentCriteria.note();
        warehouseAssignmentCriteria.otherInfo();
        warehouseAssignmentCriteria.createdBy();
        warehouseAssignmentCriteria.createdDate();
        warehouseAssignmentCriteria.lastModifiedBy();
        warehouseAssignmentCriteria.lastModifiedDate();
        warehouseAssignmentCriteria.userId();
        warehouseAssignmentCriteria.sourceWorkingUnitId();
        warehouseAssignmentCriteria.targetWorkingUnitId();
        warehouseAssignmentCriteria.orderId();
        warehouseAssignmentCriteria.distinct();
    }

    private static Condition<WarehouseAssignmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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
                condition.apply(criteria.getSourceWorkingUnitId()) &&
                condition.apply(criteria.getTargetWorkingUnitId()) &&
                condition.apply(criteria.getOrderId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WarehouseAssignmentCriteria> copyFiltersAre(
        WarehouseAssignmentCriteria copy,
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
                condition.apply(criteria.getSourceWorkingUnitId(), copy.getSourceWorkingUnitId()) &&
                condition.apply(criteria.getTargetWorkingUnitId(), copy.getTargetWorkingUnitId()) &&
                condition.apply(criteria.getOrderId(), copy.getOrderId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
