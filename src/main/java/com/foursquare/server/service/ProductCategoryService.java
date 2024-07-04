package com.foursquare.server.service;

import com.foursquare.server.domain.ProductCategory;
import com.foursquare.server.repository.ProductCategoryRepository;
import com.foursquare.server.repository.search.ProductCategorySearchRepository;
import com.foursquare.server.service.dto.ProductCategoryDTO;
import com.foursquare.server.service.mapper.ProductCategoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.ProductCategory}.
 */
@Service
@Transactional
public class ProductCategoryService {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoryService.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductCategorySearchRepository productCategorySearchRepository;

    public ProductCategoryService(
        ProductCategoryRepository productCategoryRepository,
        ProductCategoryMapper productCategoryMapper,
        ProductCategorySearchRepository productCategorySearchRepository
    ) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.productCategorySearchRepository = productCategorySearchRepository;
    }

    /**
     * Save a productCategory.
     *
     * @param productCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO) {
        log.debug("Request to save ProductCategory : {}", productCategoryDTO);
        ProductCategory productCategory = productCategoryMapper.toEntity(productCategoryDTO);
        productCategory = productCategoryRepository.save(productCategory);
        productCategorySearchRepository.index(productCategory);
        return productCategoryMapper.toDto(productCategory);
    }

    /**
     * Update a productCategory.
     *
     * @param productCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductCategoryDTO update(ProductCategoryDTO productCategoryDTO) {
        log.debug("Request to update ProductCategory : {}", productCategoryDTO);
        ProductCategory productCategory = productCategoryMapper.toEntity(productCategoryDTO);
        productCategory.setIsPersisted();
        productCategory = productCategoryRepository.save(productCategory);
        productCategorySearchRepository.index(productCategory);
        return productCategoryMapper.toDto(productCategory);
    }

    /**
     * Partially update a productCategory.
     *
     * @param productCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductCategoryDTO> partialUpdate(ProductCategoryDTO productCategoryDTO) {
        log.debug("Request to partially update ProductCategory : {}", productCategoryDTO);

        return productCategoryRepository
            .findById(productCategoryDTO.getId())
            .map(existingProductCategory -> {
                productCategoryMapper.partialUpdate(existingProductCategory, productCategoryDTO);

                return existingProductCategory;
            })
            .map(productCategoryRepository::save)
            .map(savedProductCategory -> {
                productCategorySearchRepository.index(savedProductCategory);
                return savedProductCategory;
            })
            .map(productCategoryMapper::toDto);
    }

    /**
     * Get all the productCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findAll() {
        log.debug("Request to get all ProductCategories");
        return productCategoryRepository
            .findAll()
            .stream()
            .map(productCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the productCategories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductCategoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productCategoryRepository.findAllWithEagerRelationships(pageable).map(productCategoryMapper::toDto);
    }

    /**
     * Get one productCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductCategoryDTO> findOne(UUID id) {
        log.debug("Request to get ProductCategory : {}", id);
        return productCategoryRepository.findOneWithEagerRelationships(id).map(productCategoryMapper::toDto);
    }

    /**
     * Delete the productCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete ProductCategory : {}", id);
        productCategoryRepository.deleteById(id);
        productCategorySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the productCategory corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> search(String query) {
        log.debug("Request to search ProductCategories for query {}", query);
        try {
            return StreamSupport.stream(productCategorySearchRepository.search(query).spliterator(), false)
                .map(productCategoryMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
