package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.StaffInfo;
import com.foursquare.server.repository.StaffInfoRepository;
import com.foursquare.server.service.criteria.StaffInfoCriteria;
import com.foursquare.server.service.dto.StaffInfoDTO;
import com.foursquare.server.service.mapper.StaffInfoMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StaffInfo} entities in the database.
 * The main input is a {@link StaffInfoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StaffInfoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StaffInfoQueryService extends QueryService<StaffInfo> {

    private static final Logger log = LoggerFactory.getLogger(StaffInfoQueryService.class);

    private final StaffInfoRepository staffInfoRepository;

    private final StaffInfoMapper staffInfoMapper;

    public StaffInfoQueryService(StaffInfoRepository staffInfoRepository, StaffInfoMapper staffInfoMapper) {
        this.staffInfoRepository = staffInfoRepository;
        this.staffInfoMapper = staffInfoMapper;
    }

    /**
     * Return a {@link List} of {@link StaffInfoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StaffInfoDTO> findByCriteria(StaffInfoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StaffInfo> specification = createSpecification(criteria);
        return staffInfoMapper.toDto(staffInfoRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StaffInfoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StaffInfo> specification = createSpecification(criteria);
        return staffInfoRepository.count(specification);
    }

    /**
     * Function to convert {@link StaffInfoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StaffInfo> createSpecification(StaffInfoCriteria criteria) {
        Specification<StaffInfo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StaffInfo_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), StaffInfo_.status));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildSpecification(criteria.getRole(), StaffInfo_.role));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), StaffInfo_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), StaffInfo_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), StaffInfo_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), StaffInfo_.lastModifiedDate));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(StaffInfo_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getWorkingUnitId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getWorkingUnitId(),
                        root -> root.join(StaffInfo_.workingUnit, JoinType.LEFT).get(WorkingUnit_.id)
                    )
                );
            }
        }
        return specification;
    }
}
