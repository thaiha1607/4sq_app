package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InvoiceStatusCriteriaTest {

    @Test
    void newInvoiceStatusCriteriaHasAllFiltersNullTest() {
        var invoiceStatusCriteria = new InvoiceStatusCriteria();
        assertThat(invoiceStatusCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void invoiceStatusCriteriaFluentMethodsCreatesFiltersTest() {
        var invoiceStatusCriteria = new InvoiceStatusCriteria();

        setAllFilters(invoiceStatusCriteria);

        assertThat(invoiceStatusCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void invoiceStatusCriteriaCopyCreatesNullFilterTest() {
        var invoiceStatusCriteria = new InvoiceStatusCriteria();
        var copy = invoiceStatusCriteria.copy();

        assertThat(invoiceStatusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(invoiceStatusCriteria)
        );
    }

    @Test
    void invoiceStatusCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var invoiceStatusCriteria = new InvoiceStatusCriteria();
        setAllFilters(invoiceStatusCriteria);

        var copy = invoiceStatusCriteria.copy();

        assertThat(invoiceStatusCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(invoiceStatusCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var invoiceStatusCriteria = new InvoiceStatusCriteria();

        assertThat(invoiceStatusCriteria).hasToString("InvoiceStatusCriteria{}");
    }

    private static void setAllFilters(InvoiceStatusCriteria invoiceStatusCriteria) {
        invoiceStatusCriteria.id();
        invoiceStatusCriteria.statusCode();
        invoiceStatusCriteria.description();
        invoiceStatusCriteria.createdBy();
        invoiceStatusCriteria.createdDate();
        invoiceStatusCriteria.lastModifiedBy();
        invoiceStatusCriteria.lastModifiedDate();
        invoiceStatusCriteria.distinct();
    }

    private static Condition<InvoiceStatusCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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

    private static Condition<InvoiceStatusCriteria> copyFiltersAre(
        InvoiceStatusCriteria copy,
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
