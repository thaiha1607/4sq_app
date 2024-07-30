package com.foursquare.server.service;

import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.repository.ShipmentStatusRepository;
import com.foursquare.server.repository.search.ShipmentStatusSearchRepository;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import com.foursquare.server.service.mapper.ShipmentStatusMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.ShipmentStatus}.
 */
@Service
@Transactional
public class ShipmentStatusService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentStatusService.class);

    private final ShipmentStatusRepository shipmentStatusRepository;

    private final ShipmentStatusMapper shipmentStatusMapper;

    private final ShipmentStatusSearchRepository shipmentStatusSearchRepository;

    public ShipmentStatusService(
        ShipmentStatusRepository shipmentStatusRepository,
        ShipmentStatusMapper shipmentStatusMapper,
        ShipmentStatusSearchRepository shipmentStatusSearchRepository
    ) {
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.shipmentStatusMapper = shipmentStatusMapper;
        this.shipmentStatusSearchRepository = shipmentStatusSearchRepository;
    }

    /**
     * Save a shipmentStatus.
     *
     * @param shipmentStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentStatusDTO save(ShipmentStatusDTO shipmentStatusDTO) {
        log.debug("Request to save ShipmentStatus : {}", shipmentStatusDTO);
        ShipmentStatus shipmentStatus = shipmentStatusMapper.toEntity(shipmentStatusDTO);
        shipmentStatus = shipmentStatusRepository.save(shipmentStatus);
        shipmentStatusSearchRepository.index(shipmentStatus);
        return shipmentStatusMapper.toDto(shipmentStatus);
    }

    /**
     * Update a shipmentStatus.
     *
     * @param shipmentStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentStatusDTO update(ShipmentStatusDTO shipmentStatusDTO) {
        log.debug("Request to update ShipmentStatus : {}", shipmentStatusDTO);
        ShipmentStatus shipmentStatus = shipmentStatusMapper.toEntity(shipmentStatusDTO);
        shipmentStatus.setIsPersisted();
        shipmentStatus = shipmentStatusRepository.save(shipmentStatus);
        shipmentStatusSearchRepository.index(shipmentStatus);
        return shipmentStatusMapper.toDto(shipmentStatus);
    }

    /**
     * Partially update a shipmentStatus.
     *
     * @param shipmentStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ShipmentStatusDTO> partialUpdate(ShipmentStatusDTO shipmentStatusDTO) {
        log.debug("Request to partially update ShipmentStatus : {}", shipmentStatusDTO);

        return shipmentStatusRepository
            .findById(shipmentStatusDTO.getId())
            .map(existingShipmentStatus -> {
                shipmentStatusMapper.partialUpdate(existingShipmentStatus, shipmentStatusDTO);

                return existingShipmentStatus;
            })
            .map(shipmentStatusRepository::save)
            .map(savedShipmentStatus -> {
                shipmentStatusSearchRepository.index(savedShipmentStatus);
                return savedShipmentStatus;
            })
            .map(shipmentStatusMapper::toDto);
    }

    /**
     * Get all the shipmentStatuses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentStatusDTO> findAll() {
        log.debug("Request to get all ShipmentStatuses");
        return shipmentStatusRepository
            .findAll()
            .stream()
            .map(shipmentStatusMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one shipmentStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShipmentStatusDTO> findOne(Long id) {
        log.debug("Request to get ShipmentStatus : {}", id);
        return shipmentStatusRepository.findById(id).map(shipmentStatusMapper::toDto);
    }

    /**
     * Delete the shipmentStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ShipmentStatus : {}", id);
        shipmentStatusRepository.deleteById(id);
        shipmentStatusSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the shipmentStatus corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentStatusDTO> search(String query) {
        log.debug("Request to search ShipmentStatuses for query {}", query);
        try {
            return StreamSupport.stream(shipmentStatusSearchRepository.search(query).spliterator(), false)
                .map(shipmentStatusMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
