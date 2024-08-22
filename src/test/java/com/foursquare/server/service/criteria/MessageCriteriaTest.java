package com.foursquare.server.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MessageCriteriaTest {

    @Test
    void newMessageCriteriaHasAllFiltersNullTest() {
        var messageCriteria = new MessageCriteria();
        assertThat(messageCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void messageCriteriaFluentMethodsCreatesFiltersTest() {
        var messageCriteria = new MessageCriteria();

        setAllFilters(messageCriteria);

        assertThat(messageCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void messageCriteriaCopyCreatesNullFilterTest() {
        var messageCriteria = new MessageCriteria();
        var copy = messageCriteria.copy();

        assertThat(messageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(messageCriteria)
        );
    }

    @Test
    void messageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var messageCriteria = new MessageCriteria();
        setAllFilters(messageCriteria);

        var copy = messageCriteria.copy();

        assertThat(messageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(messageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var messageCriteria = new MessageCriteria();

        assertThat(messageCriteria).hasToString("MessageCriteria{}");
    }

    private static void setAllFilters(MessageCriteria messageCriteria) {
        messageCriteria.id();
        messageCriteria.type();
        messageCriteria.content();
        messageCriteria.createdBy();
        messageCriteria.createdDate();
        messageCriteria.lastModifiedBy();
        messageCriteria.lastModifiedDate();
        messageCriteria.participantId();
        messageCriteria.seenParticipantId();
        messageCriteria.distinct();
    }

    private static Condition<MessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getParticipantId()) &&
                condition.apply(criteria.getSeenParticipantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MessageCriteria> copyFiltersAre(MessageCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getParticipantId(), copy.getParticipantId()) &&
                condition.apply(criteria.getSeenParticipantId(), copy.getSeenParticipantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
