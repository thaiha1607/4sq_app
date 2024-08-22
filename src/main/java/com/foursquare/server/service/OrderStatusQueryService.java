package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.repository.OrderStatusRepository;
import com.foursquare.server.service.criteria.OrderStatusCriteria;
import com.foursquare.server.service.dto.OrderStatusDTO;
import com.foursquare.server.service.mapper.OrderStatusMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OrderStatus} entities in the database.
 * The main input is a {@link OrderStatusCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderStatusQueryService extends QueryService<OrderStatus> {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusQueryService.class);

    private final OrderStatusRepository orderStatusRepository;

    private final OrderStatusMapper orderStatusMapper;

    public OrderStatusQueryService(OrderStatusRepository orderStatusRepository, OrderStatusMapper orderStatusMapper) {
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusMapper = orderStatusMapper;
    }

    /**
     * Return a {@link List} of {@link OrderStatusDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderStatusDTO> findByCriteria(OrderStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderStatus> specification = createSpecification(criteria);
        return orderStatusMapper.toDto(orderStatusRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderStatusCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderStatus> specification = createSpecification(criteria);
        return orderStatusRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderStatusCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderStatus> createSpecification(OrderStatusCriteria criteria) {
        Specification<OrderStatus> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderStatus_.id));
            }
            if (criteria.getStatusCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatusCode(), OrderStatus_.statusCode));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), OrderStatus_.description));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), OrderStatus_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), OrderStatus_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), OrderStatus_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), OrderStatus_.lastModifiedDate));
            }
        }
        return specification;
    }
}
