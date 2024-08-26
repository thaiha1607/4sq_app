package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.Product;
import com.foursquare.server.repository.ProductRepository;
import com.foursquare.server.service.criteria.ProductCriteria;
import com.foursquare.server.service.dto.ProductDTO;
import com.foursquare.server.service.mapper.ProductMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Product} entities in the database.
 * The main input is a {@link ProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryService extends QueryService<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductQueryService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Return a {@link List} of {@link ProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findByCriteria(ProductCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Product> specification = createSpecification(criteria);
        return productMapper.toDto(productRepository.fetchBagRelationships(productRepository.findAll(specification)));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Product> createSpecification(ProductCriteria criteria) {
        Specification<Product> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Product_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Product_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Product_.description));
            }
            if (criteria.getExpectedPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpectedPrice(), Product_.expectedPrice));
            }
            if (criteria.getProvider() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProvider(), Product_.provider));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Product_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Product_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Product_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Product_.lastModifiedDate));
            }
            if (criteria.getProductCategoryId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getProductCategoryId(),
                        root -> root.join(Product_.productCategories, JoinType.LEFT).get(ProductCategory_.id)
                    )
                );
            }
            if (criteria.getProductImageId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getProductImageId(),
                        root -> root.join(Product_.productImages, JoinType.LEFT).get(ProductImage_.id)
                    )
                );
            }
            if (criteria.getCommentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCommentId(), root -> root.join(Product_.comments, JoinType.LEFT).get(Comment_.id))
                );
            }
            if (criteria.getTagId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTagId(), root -> root.join(Product_.tags, JoinType.LEFT).get(Tag_.id))
                );
            }
        }
        return specification;
    }
}
