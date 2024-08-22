package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.ProductCategory;
import com.foursquare.server.repository.ProductCategoryRepository;
import com.foursquare.server.service.criteria.ProductCategoryCriteria;
import com.foursquare.server.service.dto.ProductCategoryDTO;
import com.foursquare.server.service.mapper.ProductCategoryMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProductCategory} entities in the database.
 * The main input is a {@link ProductCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductCategoryQueryService extends QueryService<ProductCategory> {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoryQueryService.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryQueryService(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    /**
     * Return a {@link List} of {@link ProductCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findByCriteria(ProductCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryMapper.toDto(productCategoryRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductCategory> createSpecification(ProductCategoryCriteria criteria) {
        Specification<ProductCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProductCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ProductCategory_.name));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ProductCategory_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ProductCategory_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), ProductCategory_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getLastModifiedDate(), ProductCategory_.lastModifiedDate)
                );
            }
            if (criteria.getColourId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getColourId(), root -> root.join(ProductCategory_.colour, JoinType.LEFT).get(Colour_.id))
                );
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProductId(), root -> root.join(ProductCategory_.product, JoinType.LEFT).get(Product_.id))
                );
            }
        }
        return specification;
    }
}
