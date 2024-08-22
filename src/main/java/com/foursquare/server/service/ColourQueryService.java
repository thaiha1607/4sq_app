package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.Colour;
import com.foursquare.server.repository.ColourRepository;
import com.foursquare.server.service.criteria.ColourCriteria;
import com.foursquare.server.service.dto.ColourDTO;
import com.foursquare.server.service.mapper.ColourMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Colour} entities in the database.
 * The main input is a {@link ColourCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ColourDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ColourQueryService extends QueryService<Colour> {

    private static final Logger log = LoggerFactory.getLogger(ColourQueryService.class);

    private final ColourRepository colourRepository;

    private final ColourMapper colourMapper;

    public ColourQueryService(ColourRepository colourRepository, ColourMapper colourMapper) {
        this.colourRepository = colourRepository;
        this.colourMapper = colourMapper;
    }

    /**
     * Return a {@link List} of {@link ColourDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ColourDTO> findByCriteria(ColourCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Colour> specification = createSpecification(criteria);
        return colourMapper.toDto(colourRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ColourCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Colour> specification = createSpecification(criteria);
        return colourRepository.count(specification);
    }

    /**
     * Function to convert {@link ColourCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Colour> createSpecification(ColourCriteria criteria) {
        Specification<Colour> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Colour_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Colour_.name));
            }
            if (criteria.getHexCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHexCode(), Colour_.hexCode));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Colour_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Colour_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Colour_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Colour_.lastModifiedDate));
            }
        }
        return specification;
    }
}
