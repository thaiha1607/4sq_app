package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.InternalOrderHistory;
import com.foursquare.server.repository.InternalOrderHistoryRepository;
import com.foursquare.server.service.criteria.InternalOrderHistoryCriteria;
import com.foursquare.server.service.dto.InternalOrderHistoryDTO;
import com.foursquare.server.service.mapper.InternalOrderHistoryMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InternalOrderHistory} entities in the database.
 * The main input is a {@link InternalOrderHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InternalOrderHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InternalOrderHistoryQueryService extends QueryService<InternalOrderHistory> {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderHistoryQueryService.class);

    private final InternalOrderHistoryRepository internalOrderHistoryRepository;

    private final InternalOrderHistoryMapper internalOrderHistoryMapper;

    public InternalOrderHistoryQueryService(
        InternalOrderHistoryRepository internalOrderHistoryRepository,
        InternalOrderHistoryMapper internalOrderHistoryMapper
    ) {
        this.internalOrderHistoryRepository = internalOrderHistoryRepository;
        this.internalOrderHistoryMapper = internalOrderHistoryMapper;
    }

    /**
     * Return a {@link List} of {@link InternalOrderHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InternalOrderHistoryDTO> findByCriteria(InternalOrderHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InternalOrderHistory> specification = createSpecification(criteria);
        return internalOrderHistoryMapper.toDto(internalOrderHistoryRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InternalOrderHistoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InternalOrderHistory> specification = createSpecification(criteria);
        return internalOrderHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link InternalOrderHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InternalOrderHistory> createSpecification(InternalOrderHistoryCriteria criteria) {
        Specification<InternalOrderHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), InternalOrderHistory_.id));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), InternalOrderHistory_.note));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), InternalOrderHistory_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), InternalOrderHistory_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getLastModifiedBy(), InternalOrderHistory_.lastModifiedBy)
                );
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), InternalOrderHistory_.lastModifiedDate)
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getStatusId(),
                        root -> root.join(InternalOrderHistory_.status, JoinType.LEFT).get(OrderStatus_.id)
                    )
                );
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getOrderId(),
                        root -> root.join(InternalOrderHistory_.order, JoinType.LEFT).get(InternalOrder_.id)
                    )
                );
            }
        }
        return specification;
    }
}
