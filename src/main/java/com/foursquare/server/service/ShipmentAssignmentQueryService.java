package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.repository.ShipmentAssignmentRepository;
import com.foursquare.server.service.criteria.ShipmentAssignmentCriteria;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.service.mapper.ShipmentAssignmentMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ShipmentAssignment} entities in the database.
 * The main input is a {@link ShipmentAssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShipmentAssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentAssignmentQueryService extends QueryService<ShipmentAssignment> {

    private static final Logger log = LoggerFactory.getLogger(ShipmentAssignmentQueryService.class);

    private final ShipmentAssignmentRepository shipmentAssignmentRepository;

    private final ShipmentAssignmentMapper shipmentAssignmentMapper;

    public ShipmentAssignmentQueryService(
        ShipmentAssignmentRepository shipmentAssignmentRepository,
        ShipmentAssignmentMapper shipmentAssignmentMapper
    ) {
        this.shipmentAssignmentRepository = shipmentAssignmentRepository;
        this.shipmentAssignmentMapper = shipmentAssignmentMapper;
    }

    /**
     * Return a {@link List} of {@link ShipmentAssignmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentAssignmentDTO> findByCriteria(ShipmentAssignmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShipmentAssignment> specification = createSpecification(criteria);
        return shipmentAssignmentMapper.toDto(shipmentAssignmentRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentAssignmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShipmentAssignment> specification = createSpecification(criteria);
        return shipmentAssignmentRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentAssignmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShipmentAssignment> createSpecification(ShipmentAssignmentCriteria criteria) {
        Specification<ShipmentAssignment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ShipmentAssignment_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), ShipmentAssignment_.status));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), ShipmentAssignment_.note));
            }
            if (criteria.getOtherInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOtherInfo(), ShipmentAssignment_.otherInfo));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ShipmentAssignment_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ShipmentAssignment_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getLastModifiedBy(), ShipmentAssignment_.lastModifiedBy)
                );
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), ShipmentAssignment_.lastModifiedDate)
                );
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(ShipmentAssignment_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getShipmentId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getShipmentId(),
                        root -> root.join(ShipmentAssignment_.shipment, JoinType.LEFT).get(Shipment_.id)
                    )
                );
            }
        }
        return specification;
    }
}
