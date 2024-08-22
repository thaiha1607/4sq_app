package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.ProductImage;
import com.foursquare.server.repository.ProductImageRepository;
import com.foursquare.server.service.criteria.ProductImageCriteria;
import com.foursquare.server.service.dto.ProductImageDTO;
import com.foursquare.server.service.mapper.ProductImageMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProductImage} entities in the database.
 * The main input is a {@link ProductImageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductImageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductImageQueryService extends QueryService<ProductImage> {

    private static final Logger log = LoggerFactory.getLogger(ProductImageQueryService.class);

    private final ProductImageRepository productImageRepository;

    private final ProductImageMapper productImageMapper;

    public ProductImageQueryService(ProductImageRepository productImageRepository, ProductImageMapper productImageMapper) {
        this.productImageRepository = productImageRepository;
        this.productImageMapper = productImageMapper;
    }

    /**
     * Return a {@link List} of {@link ProductImageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductImageDTO> findByCriteria(ProductImageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductImage> specification = createSpecification(criteria);
        return productImageMapper.toDto(productImageRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductImageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductImage> specification = createSpecification(criteria);
        return productImageRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductImageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductImage> createSpecification(ProductImageCriteria criteria) {
        Specification<ProductImage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProductImage_.id));
            }
            if (criteria.getImageUri() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImageUri(), ProductImage_.imageUri));
            }
            if (criteria.getAltText() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAltText(), ProductImage_.altText));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ProductImage_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ProductImage_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), ProductImage_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), ProductImage_.lastModifiedDate));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProductId(), root -> root.join(ProductImage_.product, JoinType.LEFT).get(Product_.id))
                );
            }
        }
        return specification;
    }
}
