package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.Order;
import com.foursquare.server.repository.OrderRepository;
import com.foursquare.server.service.criteria.OrderCriteria;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.mapper.OrderMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Order} entities in the database.
 * The main input is a {@link OrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService extends QueryService<Order> {

    private static final Logger log = LoggerFactory.getLogger(OrderQueryService.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public OrderQueryService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Return a {@link List} of {@link OrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findByCriteria(OrderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Order> specification = createSpecification(criteria);
        return orderMapper.toDto(orderRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Order> specification = createSpecification(criteria);
        return orderRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Order> createSpecification(OrderCriteria criteria) {
        Specification<Order> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Order_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Order_.type));
            }
            if (criteria.getPriority() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPriority(), Order_.priority));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), Order_.note));
            }
            if (criteria.getOtherInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOtherInfo(), Order_.otherInfo));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Order_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Order_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Order_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Order_.lastModifiedDate));
            }
            if (criteria.getInvoiceId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getInvoiceId(), root -> root.join(Order_.invoices, JoinType.LEFT).get(Invoice_.id))
                );
            }
            if (criteria.getOrderItemId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrderItemId(), root -> root.join(Order_.orderItems, JoinType.LEFT).get(OrderItem_.id))
                );
            }
            if (criteria.getChildOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getChildOrderId(), root -> root.join(Order_.childOrders, JoinType.LEFT).get(Order_.id))
                );
            }
            if (criteria.getInternalOrderId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getInternalOrderId(),
                        root -> root.join(Order_.internalOrders, JoinType.LEFT).get(InternalOrder_.id)
                    )
                );
            }
            if (criteria.getShipmentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getShipmentId(), root -> root.join(Order_.shipments, JoinType.LEFT).get(Shipment_.id))
                );
            }
            if (criteria.getHistoryId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getHistoryId(), root -> root.join(Order_.histories, JoinType.LEFT).get(OrderHistory_.id))
                );
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerId(), root -> root.join(Order_.customer, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getStatusId(), root -> root.join(Order_.status, JoinType.LEFT).get(OrderStatus_.id))
                );
            }
            if (criteria.getAddressId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAddressId(), root -> root.join(Order_.address, JoinType.LEFT).get(Address_.id))
                );
            }
            if (criteria.getRootOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRootOrderId(), root -> root.join(Order_.rootOrder, JoinType.LEFT).get(Order_.id))
                );
            }
        }
        return specification;
    }
}
