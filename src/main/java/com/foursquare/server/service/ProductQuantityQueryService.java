package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.ProductQuantity;
import com.foursquare.server.repository.ProductQuantityRepository;
import com.foursquare.server.service.criteria.ProductQuantityCriteria;
import com.foursquare.server.service.dto.ProductQuantityDTO;
import com.foursquare.server.service.mapper.ProductQuantityMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProductQuantity} entities in the database.
 * The main input is a {@link ProductQuantityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductQuantityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductQuantityQueryService extends QueryService<ProductQuantity> {

    private static final Logger log = LoggerFactory.getLogger(ProductQuantityQueryService.class);

    private final ProductQuantityRepository productQuantityRepository;

    private final ProductQuantityMapper productQuantityMapper;

    public ProductQuantityQueryService(ProductQuantityRepository productQuantityRepository, ProductQuantityMapper productQuantityMapper) {
        this.productQuantityRepository = productQuantityRepository;
        this.productQuantityMapper = productQuantityMapper;
    }

    /**
     * Return a {@link List} of {@link ProductQuantityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductQuantityDTO> findByCriteria(ProductQuantityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductQuantity> specification = createSpecification(criteria);
        return productQuantityMapper.toDto(productQuantityRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductQuantityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductQuantity> specification = createSpecification(criteria);
        return productQuantityRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductQuantityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductQuantity> createSpecification(ProductQuantityCriteria criteria) {
        Specification<ProductQuantity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProductQuantity_.id));
            }
            if (criteria.getQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQty(), ProductQuantity_.qty));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ProductQuantity_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ProductQuantity_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), ProductQuantity_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), ProductQuantity_.lastModifiedDate)
                );
            }
            if (criteria.getWorkingUnitId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getWorkingUnitId(),
                        root -> root.join(ProductQuantity_.workingUnit, JoinType.LEFT).get(WorkingUnit_.id)
                    )
                );
            }
            if (criteria.getProductCategoryId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getProductCategoryId(),
                        root -> root.join(ProductQuantity_.productCategory, JoinType.LEFT).get(ProductCategory_.id)
                    )
                );
            }
        }
        return specification;
    }
}
