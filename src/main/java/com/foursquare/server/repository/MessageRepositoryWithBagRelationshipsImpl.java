package com.foursquare.server.repository;

import com.foursquare.server.domain.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class MessageRepositoryWithBagRelationshipsImpl implements MessageRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String MESSAGES_PARAMETER = "messages";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Message> fetchBagRelationships(Optional<Message> message) {
        return message.map(this::fetchSeenParticipants);
    }

    @Override
    public Page<Message> fetchBagRelationships(Page<Message> messages) {
        return new PageImpl<>(fetchBagRelationships(messages.getContent()), messages.getPageable(), messages.getTotalElements());
    }

    @Override
    public List<Message> fetchBagRelationships(List<Message> messages) {
        return Optional.of(messages).map(this::fetchSeenParticipants).orElse(Collections.emptyList());
    }

    Message fetchSeenParticipants(Message result) {
        return entityManager
            .createQuery(
                "select message from Message message left join fetch message.seenParticipants where message.id = :id",
                Message.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Message> fetchSeenParticipants(List<Message> messages) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, messages.size()).forEach(index -> order.put(messages.get(index).getId(), index));
        List<Message> result = entityManager
            .createQuery(
                "select message from Message message left join fetch message.seenParticipants where message in :messages",
                Message.class
            )
            .setParameter(MESSAGES_PARAMETER, messages)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
