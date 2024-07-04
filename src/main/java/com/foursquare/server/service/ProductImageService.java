package com.foursquare.server.service;

import com.foursquare.server.service.dto.ProductImageDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Interface for managing {@link com.foursquare.server.domain.ProductImage}.
 */
public interface ProductImageService {
    /**
     * Save a productImage.
     *
     * @param productImageDTO the entity to save.
     * @return the persisted entity.
     */
    ProductImageDTO save(ProductImageDTO productImageDTO);

    /**
     * Updates a productImage.
     *
     * @param productImageDTO the entity to update.
     * @return the persisted entity.
     */
    ProductImageDTO update(ProductImageDTO productImageDTO);

    /**
     * Partially updates a productImage.
     *
     * @param productImageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductImageDTO> partialUpdate(ProductImageDTO productImageDTO);

    /**
     * Get all the productImages.
     *
     * @return the list of entities.
     */
    List<ProductImageDTO> findAll();

    /**
     * Get the "id" productImage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductImageDTO> findOne(UUID id);

    /**
     * Delete the "id" productImage.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);

    /**
     * Search for the productImage corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<ProductImageDTO> search(String query);
}
