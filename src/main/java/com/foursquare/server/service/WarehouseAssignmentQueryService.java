package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.WarehouseAssignment;
import com.foursquare.server.repository.WarehouseAssignmentRepository;
import com.foursquare.server.service.criteria.WarehouseAssignmentCriteria;
import com.foursquare.server.service.dto.WarehouseAssignmentDTO;
import com.foursquare.server.service.mapper.WarehouseAssignmentMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WarehouseAssignment} entities in the database.
 * The main input is a {@link WarehouseAssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WarehouseAssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WarehouseAssignmentQueryService extends QueryService<WarehouseAssignment> {

    private static final Logger log = LoggerFactory.getLogger(WarehouseAssignmentQueryService.class);

    private final WarehouseAssignmentRepository warehouseAssignmentRepository;

    private final WarehouseAssignmentMapper warehouseAssignmentMapper;

    public WarehouseAssignmentQueryService(
        WarehouseAssignmentRepository warehouseAssignmentRepository,
        WarehouseAssignmentMapper warehouseAssignmentMapper
    ) {
        this.warehouseAssignmentRepository = warehouseAssignmentRepository;
        this.warehouseAssignmentMapper = warehouseAssignmentMapper;
    }

    /**
     * Return a {@link List} of {@link WarehouseAssignmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseAssignmentDTO> findByCriteria(WarehouseAssignmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<WarehouseAssignment> specification = createSpecification(criteria);
        return warehouseAssignmentMapper.toDto(warehouseAssignmentRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WarehouseAssignmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WarehouseAssignment> specification = createSpecification(criteria);
        return warehouseAssignmentRepository.count(specification);
    }

    /**
     * Function to convert {@link WarehouseAssignmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WarehouseAssignment> createSpecification(WarehouseAssignmentCriteria criteria) {
        Specification<WarehouseAssignment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), WarehouseAssignment_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), WarehouseAssignment_.status));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), WarehouseAssignment_.note));
            }
            if (criteria.getOtherInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOtherInfo(), WarehouseAssignment_.otherInfo));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), WarehouseAssignment_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), WarehouseAssignment_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getLastModifiedBy(), WarehouseAssignment_.lastModifiedBy)
                );
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), WarehouseAssignment_.lastModifiedDate)
                );
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(WarehouseAssignment_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getSourceWorkingUnitId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getSourceWorkingUnitId(),
                        root -> root.join(WarehouseAssignment_.sourceWorkingUnit, JoinType.LEFT).get(WorkingUnit_.id)
                    )
                );
            }
            if (criteria.getTargetWorkingUnitId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getTargetWorkingUnitId(),
                        root -> root.join(WarehouseAssignment_.targetWorkingUnit, JoinType.LEFT).get(WorkingUnit_.id)
                    )
                );
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrderId(), root -> root.join(WarehouseAssignment_.order, JoinType.LEFT).get(Order_.id))
                );
            }
        }
        return specification;
    }
}
