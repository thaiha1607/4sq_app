package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.repository.ShipmentItemRepository;
import com.foursquare.server.service.criteria.ShipmentItemCriteria;
import com.foursquare.server.service.dto.ShipmentItemDTO;
import com.foursquare.server.service.mapper.ShipmentItemMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ShipmentItem} entities in the database.
 * The main input is a {@link ShipmentItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShipmentItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentItemQueryService extends QueryService<ShipmentItem> {

    private static final Logger log = LoggerFactory.getLogger(ShipmentItemQueryService.class);

    private final ShipmentItemRepository shipmentItemRepository;

    private final ShipmentItemMapper shipmentItemMapper;

    public ShipmentItemQueryService(ShipmentItemRepository shipmentItemRepository, ShipmentItemMapper shipmentItemMapper) {
        this.shipmentItemRepository = shipmentItemRepository;
        this.shipmentItemMapper = shipmentItemMapper;
    }

    /**
     * Return a {@link List} of {@link ShipmentItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentItemDTO> findByCriteria(ShipmentItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShipmentItem> specification = createSpecification(criteria);
        return shipmentItemMapper.toDto(shipmentItemRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShipmentItem> specification = createSpecification(criteria);
        return shipmentItemRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShipmentItem> createSpecification(ShipmentItemCriteria criteria) {
        Specification<ShipmentItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ShipmentItem_.id));
            }
            if (criteria.getQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQty(), ShipmentItem_.qty));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), ShipmentItem_.total));
            }
            if (criteria.getRollQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRollQty(), ShipmentItem_.rollQty));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ShipmentItem_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ShipmentItem_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), ShipmentItem_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), ShipmentItem_.lastModifiedDate));
            }
            if (criteria.getOrderItemId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getOrderItemId(),
                        root -> root.join(ShipmentItem_.orderItem, JoinType.LEFT).get(OrderItem_.id)
                    )
                );
            }
            if (criteria.getShipmentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getShipmentId(), root -> root.join(ShipmentItem_.shipment, JoinType.LEFT).get(Shipment_.id))
                );
            }
        }
        return specification;
    }
}
