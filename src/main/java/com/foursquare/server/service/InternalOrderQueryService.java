package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.InternalOrder;
import com.foursquare.server.repository.InternalOrderRepository;
import com.foursquare.server.service.criteria.InternalOrderCriteria;
import com.foursquare.server.service.dto.InternalOrderDTO;
import com.foursquare.server.service.mapper.InternalOrderMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InternalOrder} entities in the database.
 * The main input is a {@link InternalOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InternalOrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InternalOrderQueryService extends QueryService<InternalOrder> {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderQueryService.class);

    private final InternalOrderRepository internalOrderRepository;

    private final InternalOrderMapper internalOrderMapper;

    public InternalOrderQueryService(InternalOrderRepository internalOrderRepository, InternalOrderMapper internalOrderMapper) {
        this.internalOrderRepository = internalOrderRepository;
        this.internalOrderMapper = internalOrderMapper;
    }

    /**
     * Return a {@link List} of {@link InternalOrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InternalOrderDTO> findByCriteria(InternalOrderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InternalOrder> specification = createSpecification(criteria);
        return internalOrderMapper.toDto(internalOrderRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InternalOrderCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InternalOrder> specification = createSpecification(criteria);
        return internalOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link InternalOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InternalOrder> createSpecification(InternalOrderCriteria criteria) {
        Specification<InternalOrder> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), InternalOrder_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), InternalOrder_.type));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), InternalOrder_.note));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), InternalOrder_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), InternalOrder_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), InternalOrder_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), InternalOrder_.lastModifiedDate));
            }
            if (criteria.getHistoryId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getHistoryId(),
                        root -> root.join(InternalOrder_.histories, JoinType.LEFT).get(InternalOrderHistory_.id)
                    )
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(InternalOrder_.status, JoinType.LEFT).get(OrderStatus_.id))
                );
            }
            if (criteria.getRootOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRootOrderId(), root -> root.join(InternalOrder_.rootOrder, JoinType.LEFT).get(Order_.id))
                );
            }
        }
        return specification;
    }
}
