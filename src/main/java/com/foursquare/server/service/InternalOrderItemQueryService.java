package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.InternalOrderItem;
import com.foursquare.server.repository.InternalOrderItemRepository;
import com.foursquare.server.service.criteria.InternalOrderItemCriteria;
import com.foursquare.server.service.dto.InternalOrderItemDTO;
import com.foursquare.server.service.mapper.InternalOrderItemMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InternalOrderItem} entities in the database.
 * The main input is a {@link InternalOrderItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InternalOrderItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InternalOrderItemQueryService extends QueryService<InternalOrderItem> {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderItemQueryService.class);

    private final InternalOrderItemRepository internalOrderItemRepository;

    private final InternalOrderItemMapper internalOrderItemMapper;

    public InternalOrderItemQueryService(
        InternalOrderItemRepository internalOrderItemRepository,
        InternalOrderItemMapper internalOrderItemMapper
    ) {
        this.internalOrderItemRepository = internalOrderItemRepository;
        this.internalOrderItemMapper = internalOrderItemMapper;
    }

    /**
     * Return a {@link List} of {@link InternalOrderItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InternalOrderItemDTO> findByCriteria(InternalOrderItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InternalOrderItem> specification = createSpecification(criteria);
        return internalOrderItemMapper.toDto(internalOrderItemRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InternalOrderItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InternalOrderItem> specification = createSpecification(criteria);
        return internalOrderItemRepository.count(specification);
    }

    /**
     * Function to convert {@link InternalOrderItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InternalOrderItem> createSpecification(InternalOrderItemCriteria criteria) {
        Specification<InternalOrderItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), InternalOrderItem_.id));
            }
            if (criteria.getQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQty(), InternalOrderItem_.qty));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), InternalOrderItem_.note));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), InternalOrderItem_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), InternalOrderItem_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getLastModifiedBy(), InternalOrderItem_.lastModifiedBy)
                );
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), InternalOrderItem_.lastModifiedDate)
                );
            }
            if (criteria.getOrderItemId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getOrderItemId(),
                        root -> root.join(InternalOrderItem_.orderItem, JoinType.LEFT).get(OrderItem_.id)
                    )
                );
            }
        }
        return specification;
    }
}
