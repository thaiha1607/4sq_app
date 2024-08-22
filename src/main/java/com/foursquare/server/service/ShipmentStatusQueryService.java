package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.repository.ShipmentStatusRepository;
import com.foursquare.server.service.criteria.ShipmentStatusCriteria;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import com.foursquare.server.service.mapper.ShipmentStatusMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ShipmentStatus} entities in the database.
 * The main input is a {@link ShipmentStatusCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShipmentStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentStatusQueryService extends QueryService<ShipmentStatus> {

    private static final Logger log = LoggerFactory.getLogger(ShipmentStatusQueryService.class);

    private final ShipmentStatusRepository shipmentStatusRepository;

    private final ShipmentStatusMapper shipmentStatusMapper;

    public ShipmentStatusQueryService(ShipmentStatusRepository shipmentStatusRepository, ShipmentStatusMapper shipmentStatusMapper) {
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.shipmentStatusMapper = shipmentStatusMapper;
    }

    /**
     * Return a {@link List} of {@link ShipmentStatusDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentStatusDTO> findByCriteria(ShipmentStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShipmentStatus> specification = createSpecification(criteria);
        return shipmentStatusMapper.toDto(shipmentStatusRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentStatusCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShipmentStatus> specification = createSpecification(criteria);
        return shipmentStatusRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentStatusCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShipmentStatus> createSpecification(ShipmentStatusCriteria criteria) {
        Specification<ShipmentStatus> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ShipmentStatus_.id));
            }
            if (criteria.getStatusCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatusCode(), ShipmentStatus_.statusCode));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), ShipmentStatus_.description));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ShipmentStatus_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ShipmentStatus_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), ShipmentStatus_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), ShipmentStatus_.lastModifiedDate)
                );
            }
        }
        return specification;
    }
}
