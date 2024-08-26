package com.foursquare.server.service;

import com.foursquare.server.domain.InternalOrderHistory;
import com.foursquare.server.repository.InternalOrderHistoryRepository;
import com.foursquare.server.service.dto.InternalOrderHistoryDTO;
import com.foursquare.server.service.mapper.InternalOrderHistoryMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.InternalOrderHistory}.
 */
@Service
@Transactional
public class InternalOrderHistoryService {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderHistoryService.class);

    private final InternalOrderHistoryRepository internalOrderHistoryRepository;

    private final InternalOrderHistoryMapper internalOrderHistoryMapper;

    public InternalOrderHistoryService(
        InternalOrderHistoryRepository internalOrderHistoryRepository,
        InternalOrderHistoryMapper internalOrderHistoryMapper
    ) {
        this.internalOrderHistoryRepository = internalOrderHistoryRepository;
        this.internalOrderHistoryMapper = internalOrderHistoryMapper;
    }

    /**
     * Save a internalOrderHistory.
     *
     * @param internalOrderHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public InternalOrderHistoryDTO save(InternalOrderHistoryDTO internalOrderHistoryDTO) {
        log.debug("Request to save InternalOrderHistory : {}", internalOrderHistoryDTO);
        InternalOrderHistory internalOrderHistory = internalOrderHistoryMapper.toEntity(internalOrderHistoryDTO);
        internalOrderHistory = internalOrderHistoryRepository.save(internalOrderHistory);
        return internalOrderHistoryMapper.toDto(internalOrderHistory);
    }

    /**
     * Update a internalOrderHistory.
     *
     * @param internalOrderHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public InternalOrderHistoryDTO update(InternalOrderHistoryDTO internalOrderHistoryDTO) {
        log.debug("Request to update InternalOrderHistory : {}", internalOrderHistoryDTO);
        InternalOrderHistory internalOrderHistory = internalOrderHistoryMapper.toEntity(internalOrderHistoryDTO);
        internalOrderHistory.setIsPersisted();
        internalOrderHistory = internalOrderHistoryRepository.save(internalOrderHistory);
        return internalOrderHistoryMapper.toDto(internalOrderHistory);
    }

    /**
     * Partially update a internalOrderHistory.
     *
     * @param internalOrderHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InternalOrderHistoryDTO> partialUpdate(InternalOrderHistoryDTO internalOrderHistoryDTO) {
        log.debug("Request to partially update InternalOrderHistory : {}", internalOrderHistoryDTO);

        return internalOrderHistoryRepository
            .findById(internalOrderHistoryDTO.getId())
            .map(existingInternalOrderHistory -> {
                internalOrderHistoryMapper.partialUpdate(existingInternalOrderHistory, internalOrderHistoryDTO);

                return existingInternalOrderHistory;
            })
            .map(internalOrderHistoryRepository::save)
            .map(internalOrderHistoryMapper::toDto);
    }

    /**
     * Get all the internalOrderHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InternalOrderHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return internalOrderHistoryRepository.findAllWithEagerRelationships(pageable).map(internalOrderHistoryMapper::toDto);
    }

    /**
     * Get one internalOrderHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InternalOrderHistoryDTO> findOne(UUID id) {
        log.debug("Request to get InternalOrderHistory : {}", id);
        return internalOrderHistoryRepository.findOneWithEagerRelationships(id).map(internalOrderHistoryMapper::toDto);
    }

    /**
     * Delete the internalOrderHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete InternalOrderHistory : {}", id);
        internalOrderHistoryRepository.deleteById(id);
    }
}
