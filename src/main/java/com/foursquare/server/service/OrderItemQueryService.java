package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.repository.OrderItemRepository;
import com.foursquare.server.service.criteria.OrderItemCriteria;
import com.foursquare.server.service.dto.OrderItemDTO;
import com.foursquare.server.service.mapper.OrderItemMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OrderItem} entities in the database.
 * The main input is a {@link OrderItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderItemQueryService extends QueryService<OrderItem> {

    private static final Logger log = LoggerFactory.getLogger(OrderItemQueryService.class);

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    public OrderItemQueryService(OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Return a {@link List} of {@link OrderItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderItemDTO> findByCriteria(OrderItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderItem> specification = createSpecification(criteria);
        return orderItemMapper.toDto(orderItemRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderItem> specification = createSpecification(criteria);
        return orderItemRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderItem> createSpecification(OrderItemCriteria criteria) {
        Specification<OrderItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), OrderItem_.id));
            }
            if (criteria.getOrderedQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrderedQty(), OrderItem_.orderedQty));
            }
            if (criteria.getReceivedQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReceivedQty(), OrderItem_.receivedQty));
            }
            if (criteria.getUnitPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUnitPrice(), OrderItem_.unitPrice));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), OrderItem_.note));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), OrderItem_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), OrderItem_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), OrderItem_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), OrderItem_.lastModifiedDate));
            }
            if (criteria.getInternalOrderItemId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getInternalOrderItemId(),
                        root -> root.join(OrderItem_.internalOrderItems, JoinType.LEFT).get(InternalOrderItem_.id)
                    )
                );
            }
            if (criteria.getProductCategoryId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getProductCategoryId(),
                        root -> root.join(OrderItem_.productCategory, JoinType.LEFT).get(ProductCategory_.id)
                    )
                );
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrderId(), root -> root.join(OrderItem_.order, JoinType.LEFT).get(Order_.id))
                );
            }
        }
        return specification;
    }
}
