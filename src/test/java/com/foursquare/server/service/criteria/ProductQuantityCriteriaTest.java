package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProductQuantityCriteriaTest {

    @Test
    void newProductQuantityCriteriaHasAllFiltersNullTest() {
        var productQuantityCriteria = new ProductQuantityCriteria();
        assertThat(productQuantityCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void productQuantityCriteriaFluentMethodsCreatesFiltersTest() {
        var productQuantityCriteria = new ProductQuantityCriteria();

        setAllFilters(productQuantityCriteria);

        assertThat(productQuantityCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void productQuantityCriteriaCopyCreatesNullFilterTest() {
        var productQuantityCriteria = new ProductQuantityCriteria();
        var copy = productQuantityCriteria.copy();

        assertThat(productQuantityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(productQuantityCriteria)
        );
    }

    @Test
    void productQuantityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var productQuantityCriteria = new ProductQuantityCriteria();
        setAllFilters(productQuantityCriteria);

        var copy = productQuantityCriteria.copy();

        assertThat(productQuantityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(productQuantityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var productQuantityCriteria = new ProductQuantityCriteria();

        assertThat(productQuantityCriteria).hasToString("ProductQuantityCriteria{}");
    }

    private static void setAllFilters(ProductQuantityCriteria productQuantityCriteria) {
        productQuantityCriteria.id();
        productQuantityCriteria.qty();
        productQuantityCriteria.createdBy();
        productQuantityCriteria.createdDate();
        productQuantityCriteria.lastModifiedBy();
        productQuantityCriteria.lastModifiedDate();
        productQuantityCriteria.workingUnitId();
        productQuantityCriteria.productCategoryId();
        productQuantityCriteria.distinct();
    }

    private static Condition<ProductQuantityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQty()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getWorkingUnitId()) &&
                condition.apply(criteria.getProductCategoryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProductQuantityCriteria> copyFiltersAre(
        ProductQuantityCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQty(), copy.getQty()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getWorkingUnitId(), copy.getWorkingUnitId()) &&
                condition.apply(criteria.getProductCategoryId(), copy.getProductCategoryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
