package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.Conversation;
import com.foursquare.server.repository.ConversationRepository;
import com.foursquare.server.service.criteria.ConversationCriteria;
import com.foursquare.server.service.dto.ConversationDTO;
import com.foursquare.server.service.mapper.ConversationMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Conversation} entities in the database.
 * The main input is a {@link ConversationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ConversationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ConversationQueryService extends QueryService<Conversation> {

    private static final Logger log = LoggerFactory.getLogger(ConversationQueryService.class);

    private final ConversationRepository conversationRepository;

    private final ConversationMapper conversationMapper;

    public ConversationQueryService(ConversationRepository conversationRepository, ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
    }

    /**
     * Return a {@link List} of {@link ConversationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> findByCriteria(ConversationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Conversation> specification = createSpecification(criteria);
        return conversationMapper.toDto(conversationRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ConversationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Conversation> specification = createSpecification(criteria);
        return conversationRepository.count(specification);
    }

    /**
     * Function to convert {@link ConversationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Conversation> createSpecification(ConversationCriteria criteria) {
        Specification<Conversation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Conversation_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Conversation_.title));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Conversation_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Conversation_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Conversation_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Conversation_.lastModifiedDate));
            }
            if (criteria.getParticipantId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getParticipantId(),
                        root -> root.join(Conversation_.participants, JoinType.LEFT).get(Participant_.id)
                    )
                );
            }
        }
        return specification;
    }
}
