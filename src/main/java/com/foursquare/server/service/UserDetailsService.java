package com.foursquare.server.service;

import com.foursquare.server.service.dto.UserDetailsDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.foursquare.server.domain.UserDetails}.
 */
public interface UserDetailsService {
    /**
     * Save a userDetails.
     *
     * @param userDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    UserDetailsDTO save(UserDetailsDTO userDetailsDTO);

    /**
     * Updates a userDetails.
     *
     * @param userDetailsDTO the entity to update.
     * @return the persisted entity.
     */
    UserDetailsDTO update(UserDetailsDTO userDetailsDTO);

    /**
     * Partially updates a userDetails.
     *
     * @param userDetailsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserDetailsDTO> partialUpdate(UserDetailsDTO userDetailsDTO);

    /**
     * Get all the userDetails.
     *
     * @return the list of entities.
     */
    List<UserDetailsDTO> findAll();

    /**
     * Get all the userDetails with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserDetailsDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" userDetails.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserDetailsDTO> findOne(Long id);

    /**
     * Delete the "id" userDetails.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
