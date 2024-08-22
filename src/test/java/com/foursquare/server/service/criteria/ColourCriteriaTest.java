package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ColourCriteriaTest {

    @Test
    void newColourCriteriaHasAllFiltersNullTest() {
        var colourCriteria = new ColourCriteria();
        assertThat(colourCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void colourCriteriaFluentMethodsCreatesFiltersTest() {
        var colourCriteria = new ColourCriteria();

        setAllFilters(colourCriteria);

        assertThat(colourCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void colourCriteriaCopyCreatesNullFilterTest() {
        var colourCriteria = new ColourCriteria();
        var copy = colourCriteria.copy();

        assertThat(colourCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(colourCriteria)
        );
    }

    @Test
    void colourCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var colourCriteria = new ColourCriteria();
        setAllFilters(colourCriteria);

        var copy = colourCriteria.copy();

        assertThat(colourCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(colourCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var colourCriteria = new ColourCriteria();

        assertThat(colourCriteria).hasToString("ColourCriteria{}");
    }

    private static void setAllFilters(ColourCriteria colourCriteria) {
        colourCriteria.id();
        colourCriteria.name();
        colourCriteria.hexCode();
        colourCriteria.createdBy();
        colourCriteria.createdDate();
        colourCriteria.lastModifiedBy();
        colourCriteria.lastModifiedDate();
        colourCriteria.distinct();
    }

    private static Condition<ColourCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getHexCode()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ColourCriteria> copyFiltersAre(ColourCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getHexCode(), copy.getHexCode()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
