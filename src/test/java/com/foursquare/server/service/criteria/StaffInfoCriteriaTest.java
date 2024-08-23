package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StaffInfoCriteriaTest {

    @Test
    void newStaffInfoCriteriaHasAllFiltersNullTest() {
        var staffInfoCriteria = new StaffInfoCriteria();
        assertThat(staffInfoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void staffInfoCriteriaFluentMethodsCreatesFiltersTest() {
        var staffInfoCriteria = new StaffInfoCriteria();

        setAllFilters(staffInfoCriteria);

        assertThat(staffInfoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void staffInfoCriteriaCopyCreatesNullFilterTest() {
        var staffInfoCriteria = new StaffInfoCriteria();
        var copy = staffInfoCriteria.copy();

        assertThat(staffInfoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(staffInfoCriteria)
        );
    }

    @Test
    void staffInfoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var staffInfoCriteria = new StaffInfoCriteria();
        setAllFilters(staffInfoCriteria);

        var copy = staffInfoCriteria.copy();

        assertThat(staffInfoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(staffInfoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var staffInfoCriteria = new StaffInfoCriteria();

        assertThat(staffInfoCriteria).hasToString("StaffInfoCriteria{}");
    }

    private static void setAllFilters(StaffInfoCriteria staffInfoCriteria) {
        staffInfoCriteria.id();
        staffInfoCriteria.status();
        staffInfoCriteria.role();
        staffInfoCriteria.createdBy();
        staffInfoCriteria.createdDate();
        staffInfoCriteria.lastModifiedBy();
        staffInfoCriteria.lastModifiedDate();
        staffInfoCriteria.userId();
        staffInfoCriteria.workingUnitId();
        staffInfoCriteria.distinct();
    }

    private static Condition<StaffInfoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getWorkingUnitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StaffInfoCriteria> copyFiltersAre(StaffInfoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getWorkingUnitId(), copy.getWorkingUnitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
