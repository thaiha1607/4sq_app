package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UserDetailsCriteriaTest {

    @Test
    void newUserDetailsCriteriaHasAllFiltersNullTest() {
        var userDetailsCriteria = new UserDetailsCriteria();
        assertThat(userDetailsCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void userDetailsCriteriaFluentMethodsCreatesFiltersTest() {
        var userDetailsCriteria = new UserDetailsCriteria();

        setAllFilters(userDetailsCriteria);

        assertThat(userDetailsCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void userDetailsCriteriaCopyCreatesNullFilterTest() {
        var userDetailsCriteria = new UserDetailsCriteria();
        var copy = userDetailsCriteria.copy();

        assertThat(userDetailsCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(userDetailsCriteria)
        );
    }

    @Test
    void userDetailsCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var userDetailsCriteria = new UserDetailsCriteria();
        setAllFilters(userDetailsCriteria);

        var copy = userDetailsCriteria.copy();

        assertThat(userDetailsCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(userDetailsCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var userDetailsCriteria = new UserDetailsCriteria();

        assertThat(userDetailsCriteria).hasToString("UserDetailsCriteria{}");
    }

    private static void setAllFilters(UserDetailsCriteria userDetailsCriteria) {
        userDetailsCriteria.id();
        userDetailsCriteria.phone();
        userDetailsCriteria.createdBy();
        userDetailsCriteria.createdDate();
        userDetailsCriteria.lastModifiedBy();
        userDetailsCriteria.lastModifiedDate();
        userDetailsCriteria.userId();
        userDetailsCriteria.distinct();
    }

    private static Condition<UserDetailsCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UserDetailsCriteria> copyFiltersAre(UserDetailsCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
