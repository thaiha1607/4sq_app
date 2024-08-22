package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.Participant;
import com.foursquare.server.repository.ParticipantRepository;
import com.foursquare.server.service.criteria.ParticipantCriteria;
import com.foursquare.server.service.dto.ParticipantDTO;
import com.foursquare.server.service.mapper.ParticipantMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Participant} entities in the database.
 * The main input is a {@link ParticipantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ParticipantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantQueryService extends QueryService<Participant> {

    private static final Logger log = LoggerFactory.getLogger(ParticipantQueryService.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;

    public ParticipantQueryService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Return a {@link List} of {@link ParticipantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findByCriteria(ParticipantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantMapper.toDto(participantRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ParticipantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantRepository.count(specification);
    }

    /**
     * Function to convert {@link ParticipantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Participant> createSpecification(ParticipantCriteria criteria) {
        Specification<Participant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Participant_.id));
            }
            if (criteria.getIsAdmin() != null) {
                specification = specification.and(buildSpecification(criteria.getIsAdmin(), Participant_.isAdmin));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Participant_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Participant_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Participant_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Participant_.lastModifiedDate));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Participant_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getConversationId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getConversationId(),
                        root -> root.join(Participant_.conversation, JoinType.LEFT).get(Conversation_.id)
                    )
                );
            }
            if (criteria.getMessageId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getMessageId(), root -> root.join(Participant_.messages, JoinType.LEFT).get(Message_.id))
                );
            }
            if (criteria.getSeenMessageId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getSeenMessageId(),
                        root -> root.join(Participant_.seenMessages, JoinType.LEFT).get(Message_.id)
                    )
                );
            }
        }
        return specification;
    }
}
