package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParticipantCriteriaTest {

    @Test
    void newParticipantCriteriaHasAllFiltersNullTest() {
        var participantCriteria = new ParticipantCriteria();
        assertThat(participantCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void participantCriteriaFluentMethodsCreatesFiltersTest() {
        var participantCriteria = new ParticipantCriteria();

        setAllFilters(participantCriteria);

        assertThat(participantCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void participantCriteriaCopyCreatesNullFilterTest() {
        var participantCriteria = new ParticipantCriteria();
        var copy = participantCriteria.copy();

        assertThat(participantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(participantCriteria)
        );
    }

    @Test
    void participantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var participantCriteria = new ParticipantCriteria();
        setAllFilters(participantCriteria);

        var copy = participantCriteria.copy();

        assertThat(participantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(participantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var participantCriteria = new ParticipantCriteria();

        assertThat(participantCriteria).hasToString("ParticipantCriteria{}");
    }

    private static void setAllFilters(ParticipantCriteria participantCriteria) {
        participantCriteria.id();
        participantCriteria.isAdmin();
        participantCriteria.createdBy();
        participantCriteria.createdDate();
        participantCriteria.lastModifiedBy();
        participantCriteria.lastModifiedDate();
        participantCriteria.userId();
        participantCriteria.conversationId();
        participantCriteria.messageId();
        participantCriteria.seenMessageId();
        participantCriteria.distinct();
    }

    private static Condition<ParticipantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIsAdmin()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getConversationId()) &&
                condition.apply(criteria.getMessageId()) &&
                condition.apply(criteria.getSeenMessageId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParticipantCriteria> copyFiltersAre(ParticipantCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIsAdmin(), copy.getIsAdmin()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getConversationId(), copy.getConversationId()) &&
                condition.apply(criteria.getMessageId(), copy.getMessageId()) &&
                condition.apply(criteria.getSeenMessageId(), copy.getSeenMessageId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
