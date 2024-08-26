package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.OrderHistory;
import com.foursquare.server.repository.OrderHistoryRepository;
import com.foursquare.server.service.criteria.OrderHistoryCriteria;
import com.foursquare.server.service.dto.OrderHistoryDTO;
import com.foursquare.server.service.mapper.OrderHistoryMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OrderHistory} entities in the database.
 * The main input is a {@link OrderHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderHistoryQueryService extends QueryService<OrderHistory> {

    private static final Logger log = LoggerFactory.getLogger(OrderHistoryQueryService.class);

    private final OrderHistoryRepository orderHistoryRepository;

    private final OrderHistoryMapper orderHistoryMapper;

    public OrderHistoryQueryService(OrderHistoryRepository orderHistoryRepository, OrderHistoryMapper orderHistoryMapper) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderHistoryMapper = orderHistoryMapper;
    }

    /**
     * Return a {@link List} of {@link OrderHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryDTO> findByCriteria(OrderHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderHistory> specification = createSpecification(criteria);
        return orderHistoryMapper.toDto(orderHistoryRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderHistoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderHistory> specification = createSpecification(criteria);
        return orderHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderHistory> createSpecification(OrderHistoryCriteria criteria) {
        Specification<OrderHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), OrderHistory_.id));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), OrderHistory_.note));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), OrderHistory_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), OrderHistory_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), OrderHistory_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), OrderHistory_.lastModifiedDate));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(OrderHistory_.status, JoinType.LEFT).get(OrderStatus_.id))
                );
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrderId(), root -> root.join(OrderHistory_.order, JoinType.LEFT).get(Order_.id))
                );
            }
        }
        return specification;
    }
}
