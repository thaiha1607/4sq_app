package com.foursquare.server.service;

import com.foursquare.server.domain.ProductQuantity;
import com.foursquare.server.repository.ProductQuantityRepository;
import com.foursquare.server.repository.search.ProductQuantitySearchRepository;
import com.foursquare.server.service.dto.ProductQuantityDTO;
import com.foursquare.server.service.mapper.ProductQuantityMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.ProductQuantity}.
 */
@Service
@Transactional
public class ProductQuantityService {

    private static final Logger log = LoggerFactory.getLogger(ProductQuantityService.class);

    private final ProductQuantityRepository productQuantityRepository;

    private final ProductQuantityMapper productQuantityMapper;

    private final ProductQuantitySearchRepository productQuantitySearchRepository;

    public ProductQuantityService(
        ProductQuantityRepository productQuantityRepository,
        ProductQuantityMapper productQuantityMapper,
        ProductQuantitySearchRepository productQuantitySearchRepository
    ) {
        this.productQuantityRepository = productQuantityRepository;
        this.productQuantityMapper = productQuantityMapper;
        this.productQuantitySearchRepository = productQuantitySearchRepository;
    }

    /**
     * Save a productQuantity.
     *
     * @param productQuantityDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductQuantityDTO save(ProductQuantityDTO productQuantityDTO) {
        log.debug("Request to save ProductQuantity : {}", productQuantityDTO);
        ProductQuantity productQuantity = productQuantityMapper.toEntity(productQuantityDTO);
        productQuantity = productQuantityRepository.save(productQuantity);
        productQuantitySearchRepository.index(productQuantity);
        return productQuantityMapper.toDto(productQuantity);
    }

    /**
     * Update a productQuantity.
     *
     * @param productQuantityDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductQuantityDTO update(ProductQuantityDTO productQuantityDTO) {
        log.debug("Request to update ProductQuantity : {}", productQuantityDTO);
        ProductQuantity productQuantity = productQuantityMapper.toEntity(productQuantityDTO);
        productQuantity.setIsPersisted();
        productQuantity = productQuantityRepository.save(productQuantity);
        productQuantitySearchRepository.index(productQuantity);
        return productQuantityMapper.toDto(productQuantity);
    }

    /**
     * Partially update a productQuantity.
     *
     * @param productQuantityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductQuantityDTO> partialUpdate(ProductQuantityDTO productQuantityDTO) {
        log.debug("Request to partially update ProductQuantity : {}", productQuantityDTO);

        return productQuantityRepository
            .findById(productQuantityDTO.getId())
            .map(existingProductQuantity -> {
                productQuantityMapper.partialUpdate(existingProductQuantity, productQuantityDTO);

                return existingProductQuantity;
            })
            .map(productQuantityRepository::save)
            .map(savedProductQuantity -> {
                productQuantitySearchRepository.index(savedProductQuantity);
                return savedProductQuantity;
            })
            .map(productQuantityMapper::toDto);
    }

    /**
     * Get all the productQuantities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProductQuantityDTO> findAll() {
        log.debug("Request to get all ProductQuantities");
        return productQuantityRepository
            .findAll()
            .stream()
            .map(productQuantityMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the productQuantities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductQuantityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productQuantityRepository.findAllWithEagerRelationships(pageable).map(productQuantityMapper::toDto);
    }

    /**
     * Get one productQuantity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductQuantityDTO> findOne(UUID id) {
        log.debug("Request to get ProductQuantity : {}", id);
        return productQuantityRepository.findOneWithEagerRelationships(id).map(productQuantityMapper::toDto);
    }

    /**
     * Delete the productQuantity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete ProductQuantity : {}", id);
        productQuantityRepository.deleteById(id);
        productQuantitySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the productQuantity corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProductQuantityDTO> search(String query) {
        log.debug("Request to search ProductQuantities for query {}", query);
        try {
            return StreamSupport.stream(productQuantitySearchRepository.search(query).spliterator(), false)
                .map(productQuantityMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
