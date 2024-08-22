package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ConversationCriteriaTest {

    @Test
    void newConversationCriteriaHasAllFiltersNullTest() {
        var conversationCriteria = new ConversationCriteria();
        assertThat(conversationCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void conversationCriteriaFluentMethodsCreatesFiltersTest() {
        var conversationCriteria = new ConversationCriteria();

        setAllFilters(conversationCriteria);

        assertThat(conversationCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void conversationCriteriaCopyCreatesNullFilterTest() {
        var conversationCriteria = new ConversationCriteria();
        var copy = conversationCriteria.copy();

        assertThat(conversationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(conversationCriteria)
        );
    }

    @Test
    void conversationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var conversationCriteria = new ConversationCriteria();
        setAllFilters(conversationCriteria);

        var copy = conversationCriteria.copy();

        assertThat(conversationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(conversationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var conversationCriteria = new ConversationCriteria();

        assertThat(conversationCriteria).hasToString("ConversationCriteria{}");
    }

    private static void setAllFilters(ConversationCriteria conversationCriteria) {
        conversationCriteria.id();
        conversationCriteria.title();
        conversationCriteria.createdBy();
        conversationCriteria.createdDate();
        conversationCriteria.lastModifiedBy();
        conversationCriteria.lastModifiedDate();
        conversationCriteria.participantId();
        conversationCriteria.distinct();
    }

    private static Condition<ConversationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getParticipantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ConversationCriteria> copyFiltersAre(
        ConversationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getParticipantId(), copy.getParticipantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
