package com.foursquare.server.service;

import com.foursquare.server.service.dto.UserAddressDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.foursquare.server.domain.UserAddress}.
 */
public interface UserAddressService {
    /**
     * Save a userAddress.
     *
     * @param userAddressDTO the entity to save.
     * @return the persisted entity.
     */
    UserAddressDTO save(UserAddressDTO userAddressDTO);

    /**
     * Updates a userAddress.
     *
     * @param userAddressDTO the entity to update.
     * @return the persisted entity.
     */
    UserAddressDTO update(UserAddressDTO userAddressDTO);

    /**
     * Partially updates a userAddress.
     *
     * @param userAddressDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserAddressDTO> partialUpdate(UserAddressDTO userAddressDTO);

    /**
     * Get all the userAddresses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserAddressDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" userAddress.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserAddressDTO> findOne(UUID id);

    /**
     * Delete the "id" userAddress.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);
}
