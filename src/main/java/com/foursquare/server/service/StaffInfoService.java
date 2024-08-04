package com.foursquare.server.service;

import com.foursquare.server.service.dto.StaffInfoDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.foursquare.server.domain.StaffInfo}.
 */
public interface StaffInfoService {
    /**
     * Save a staffInfo.
     *
     * @param staffInfoDTO the entity to save.
     * @return the persisted entity.
     */
    StaffInfoDTO save(StaffInfoDTO staffInfoDTO);

    /**
     * Updates a staffInfo.
     *
     * @param staffInfoDTO the entity to update.
     * @return the persisted entity.
     */
    StaffInfoDTO update(StaffInfoDTO staffInfoDTO);

    /**
     * Partially updates a staffInfo.
     *
     * @param staffInfoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StaffInfoDTO> partialUpdate(StaffInfoDTO staffInfoDTO);

    /**
     * Get all the staffInfos.
     *
     * @return the list of entities.
     */
    List<StaffInfoDTO> findAll();

    /**
     * Get all the staffInfos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StaffInfoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" staffInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StaffInfoDTO> findOne(Long id);

    /**
     * Delete the "id" staffInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the staffInfo corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<StaffInfoDTO> search(String query);
}
