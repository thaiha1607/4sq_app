package com.foursquare.server.service;

import com.foursquare.server.service.dto.ProductImageDTO;
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
}
