package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UserAddressCriteriaTest {

    @Test
    void newUserAddressCriteriaHasAllFiltersNullTest() {
        var userAddressCriteria = new UserAddressCriteria();
        assertThat(userAddressCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void userAddressCriteriaFluentMethodsCreatesFiltersTest() {
        var userAddressCriteria = new UserAddressCriteria();

        setAllFilters(userAddressCriteria);

        assertThat(userAddressCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void userAddressCriteriaCopyCreatesNullFilterTest() {
        var userAddressCriteria = new UserAddressCriteria();
        var copy = userAddressCriteria.copy();

        assertThat(userAddressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(userAddressCriteria)
        );
    }

    @Test
    void userAddressCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var userAddressCriteria = new UserAddressCriteria();
        setAllFilters(userAddressCriteria);

        var copy = userAddressCriteria.copy();

        assertThat(userAddressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(userAddressCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var userAddressCriteria = new UserAddressCriteria();

        assertThat(userAddressCriteria).hasToString("UserAddressCriteria{}");
    }

    private static void setAllFilters(UserAddressCriteria userAddressCriteria) {
        userAddressCriteria.id();
        userAddressCriteria.type();
        userAddressCriteria.friendlyName();
        userAddressCriteria.isDefault();
        userAddressCriteria.createdBy();
        userAddressCriteria.createdDate();
        userAddressCriteria.lastModifiedBy();
        userAddressCriteria.lastModifiedDate();
        userAddressCriteria.userId();
        userAddressCriteria.addressId();
        userAddressCriteria.distinct();
    }

    private static Condition<UserAddressCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getFriendlyName()) &&
                condition.apply(criteria.getIsDefault()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getAddressId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UserAddressCriteria> copyFiltersAre(UserAddressCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getFriendlyName(), copy.getFriendlyName()) &&
                condition.apply(criteria.getIsDefault(), copy.getIsDefault()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getAddressId(), copy.getAddressId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
