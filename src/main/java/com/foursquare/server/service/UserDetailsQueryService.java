package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.UserDetails;
import com.foursquare.server.repository.UserDetailsRepository;
import com.foursquare.server.service.criteria.UserDetailsCriteria;
import com.foursquare.server.service.dto.UserDetailsDTO;
import com.foursquare.server.service.mapper.UserDetailsMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserDetails} entities in the database.
 * The main input is a {@link UserDetailsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserDetailsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsQueryService extends QueryService<UserDetails> {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsQueryService.class);

    private final UserDetailsRepository userDetailsRepository;

    private final UserDetailsMapper userDetailsMapper;

    public UserDetailsQueryService(UserDetailsRepository userDetailsRepository, UserDetailsMapper userDetailsMapper) {
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsMapper = userDetailsMapper;
    }

    /**
     * Return a {@link List} of {@link UserDetailsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserDetailsDTO> findByCriteria(UserDetailsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserDetails> specification = createSpecification(criteria);
        return userDetailsMapper.toDto(userDetailsRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserDetailsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserDetails> specification = createSpecification(criteria);
        return userDetailsRepository.count(specification);
    }

    /**
     * Function to convert {@link UserDetailsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserDetails> createSpecification(UserDetailsCriteria criteria) {
        Specification<UserDetails> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserDetails_.id));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), UserDetails_.phone));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), UserDetails_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), UserDetails_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), UserDetails_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), UserDetails_.lastModifiedDate));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(UserDetails_.user, JoinType.LEFT).get(User_.id))
                );
            }
        }
        return specification;
    }
}
