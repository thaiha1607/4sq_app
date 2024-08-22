package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.repository.WorkingUnitRepository;
import com.foursquare.server.service.criteria.WorkingUnitCriteria;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import com.foursquare.server.service.mapper.WorkingUnitMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WorkingUnit} entities in the database.
 * The main input is a {@link WorkingUnitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkingUnitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkingUnitQueryService extends QueryService<WorkingUnit> {

    private static final Logger log = LoggerFactory.getLogger(WorkingUnitQueryService.class);

    private final WorkingUnitRepository workingUnitRepository;

    private final WorkingUnitMapper workingUnitMapper;

    public WorkingUnitQueryService(WorkingUnitRepository workingUnitRepository, WorkingUnitMapper workingUnitMapper) {
        this.workingUnitRepository = workingUnitRepository;
        this.workingUnitMapper = workingUnitMapper;
    }

    /**
     * Return a {@link List} of {@link WorkingUnitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkingUnitDTO> findByCriteria(WorkingUnitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<WorkingUnit> specification = createSpecification(criteria);
        return workingUnitMapper.toDto(workingUnitRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkingUnitCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WorkingUnit> specification = createSpecification(criteria);
        return workingUnitRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkingUnitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WorkingUnit> createSpecification(WorkingUnitCriteria criteria) {
        Specification<WorkingUnit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), WorkingUnit_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), WorkingUnit_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), WorkingUnit_.type));
            }
            if (criteria.getImageUri() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImageUri(), WorkingUnit_.imageUri));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), WorkingUnit_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), WorkingUnit_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), WorkingUnit_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), WorkingUnit_.lastModifiedDate));
            }
            if (criteria.getAddressId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAddressId(), root -> root.join(WorkingUnit_.address, JoinType.LEFT).get(Address_.id))
                );
            }
        }
        return specification;
    }
}
