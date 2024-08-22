package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WorkingUnitCriteriaTest {

    @Test
    void newWorkingUnitCriteriaHasAllFiltersNullTest() {
        var workingUnitCriteria = new WorkingUnitCriteria();
        assertThat(workingUnitCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void workingUnitCriteriaFluentMethodsCreatesFiltersTest() {
        var workingUnitCriteria = new WorkingUnitCriteria();

        setAllFilters(workingUnitCriteria);

        assertThat(workingUnitCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void workingUnitCriteriaCopyCreatesNullFilterTest() {
        var workingUnitCriteria = new WorkingUnitCriteria();
        var copy = workingUnitCriteria.copy();

        assertThat(workingUnitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(workingUnitCriteria)
        );
    }

    @Test
    void workingUnitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var workingUnitCriteria = new WorkingUnitCriteria();
        setAllFilters(workingUnitCriteria);

        var copy = workingUnitCriteria.copy();

        assertThat(workingUnitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(workingUnitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var workingUnitCriteria = new WorkingUnitCriteria();

        assertThat(workingUnitCriteria).hasToString("WorkingUnitCriteria{}");
    }

    private static void setAllFilters(WorkingUnitCriteria workingUnitCriteria) {
        workingUnitCriteria.id();
        workingUnitCriteria.name();
        workingUnitCriteria.type();
        workingUnitCriteria.imageUri();
        workingUnitCriteria.createdBy();
        workingUnitCriteria.createdDate();
        workingUnitCriteria.lastModifiedBy();
        workingUnitCriteria.lastModifiedDate();
        workingUnitCriteria.addressId();
        workingUnitCriteria.distinct();
    }

    private static Condition<WorkingUnitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getImageUri()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getAddressId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WorkingUnitCriteria> copyFiltersAre(WorkingUnitCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getImageUri(), copy.getImageUri()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getAddressId(), copy.getAddressId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
