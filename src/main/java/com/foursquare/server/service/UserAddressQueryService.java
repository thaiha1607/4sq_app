package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.UserAddress;
import com.foursquare.server.repository.UserAddressRepository;
import com.foursquare.server.service.criteria.UserAddressCriteria;
import com.foursquare.server.service.dto.UserAddressDTO;
import com.foursquare.server.service.mapper.UserAddressMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserAddress} entities in the database.
 * The main input is a {@link UserAddressCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserAddressDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserAddressQueryService extends QueryService<UserAddress> {

    private static final Logger log = LoggerFactory.getLogger(UserAddressQueryService.class);

    private final UserAddressRepository userAddressRepository;

    private final UserAddressMapper userAddressMapper;

    public UserAddressQueryService(UserAddressRepository userAddressRepository, UserAddressMapper userAddressMapper) {
        this.userAddressRepository = userAddressRepository;
        this.userAddressMapper = userAddressMapper;
    }

    /**
     * Return a {@link List} of {@link UserAddressDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserAddressDTO> findByCriteria(UserAddressCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserAddress> specification = createSpecification(criteria);
        return userAddressMapper.toDto(userAddressRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserAddressCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserAddress> specification = createSpecification(criteria);
        return userAddressRepository.count(specification);
    }

    /**
     * Function to convert {@link UserAddressCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserAddress> createSpecification(UserAddressCriteria criteria) {
        Specification<UserAddress> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), UserAddress_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), UserAddress_.type));
            }
            if (criteria.getFriendlyName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFriendlyName(), UserAddress_.friendlyName));
            }
            if (criteria.getIsDefault() != null) {
                specification = specification.and(buildSpecification(criteria.getIsDefault(), UserAddress_.isDefault));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), UserAddress_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), UserAddress_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), UserAddress_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), UserAddress_.lastModifiedDate));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(UserAddress_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getAddressId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAddressId(), root -> root.join(UserAddress_.address, JoinType.LEFT).get(Address_.id))
                );
            }
        }
        return specification;
    }
}
