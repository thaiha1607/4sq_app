package com.foursquare.server.service;

import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.repository.ShipmentAssignmentRepository;
import com.foursquare.server.repository.search.ShipmentAssignmentSearchRepository;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.service.mapper.ShipmentAssignmentMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.ShipmentAssignment}.
 */
@Service
@Transactional
public class ShipmentAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentAssignmentService.class);

    private final ShipmentAssignmentRepository shipmentAssignmentRepository;

    private final ShipmentAssignmentMapper shipmentAssignmentMapper;

    private final ShipmentAssignmentSearchRepository shipmentAssignmentSearchRepository;

    public ShipmentAssignmentService(
        ShipmentAssignmentRepository shipmentAssignmentRepository,
        ShipmentAssignmentMapper shipmentAssignmentMapper,
        ShipmentAssignmentSearchRepository shipmentAssignmentSearchRepository
    ) {
        this.shipmentAssignmentRepository = shipmentAssignmentRepository;
        this.shipmentAssignmentMapper = shipmentAssignmentMapper;
        this.shipmentAssignmentSearchRepository = shipmentAssignmentSearchRepository;
    }

    /**
     * Save a shipmentAssignment.
     *
     * @param shipmentAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentAssignmentDTO save(ShipmentAssignmentDTO shipmentAssignmentDTO) {
        log.debug("Request to save ShipmentAssignment : {}", shipmentAssignmentDTO);
        ShipmentAssignment shipmentAssignment = shipmentAssignmentMapper.toEntity(shipmentAssignmentDTO);
        shipmentAssignment = shipmentAssignmentRepository.save(shipmentAssignment);
        shipmentAssignmentSearchRepository.index(shipmentAssignment);
        return shipmentAssignmentMapper.toDto(shipmentAssignment);
    }

    /**
     * Update a shipmentAssignment.
     *
     * @param shipmentAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentAssignmentDTO update(ShipmentAssignmentDTO shipmentAssignmentDTO) {
        log.debug("Request to update ShipmentAssignment : {}", shipmentAssignmentDTO);
        ShipmentAssignment shipmentAssignment = shipmentAssignmentMapper.toEntity(shipmentAssignmentDTO);
        shipmentAssignment.setIsPersisted();
        shipmentAssignment = shipmentAssignmentRepository.save(shipmentAssignment);
        shipmentAssignmentSearchRepository.index(shipmentAssignment);
        return shipmentAssignmentMapper.toDto(shipmentAssignment);
    }

    /**
     * Partially update a shipmentAssignment.
     *
     * @param shipmentAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ShipmentAssignmentDTO> partialUpdate(ShipmentAssignmentDTO shipmentAssignmentDTO) {
        log.debug("Request to partially update ShipmentAssignment : {}", shipmentAssignmentDTO);

        return shipmentAssignmentRepository
            .findById(shipmentAssignmentDTO.getId())
            .map(existingShipmentAssignment -> {
                shipmentAssignmentMapper.partialUpdate(existingShipmentAssignment, shipmentAssignmentDTO);

                return existingShipmentAssignment;
            })
            .map(shipmentAssignmentRepository::save)
            .map(savedShipmentAssignment -> {
                shipmentAssignmentSearchRepository.index(savedShipmentAssignment);
                return savedShipmentAssignment;
            })
            .map(shipmentAssignmentMapper::toDto);
    }

    /**
     * Get all the shipmentAssignments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentAssignmentDTO> findAll() {
        log.debug("Request to get all ShipmentAssignments");
        return shipmentAssignmentRepository
            .findAll()
            .stream()
            .map(shipmentAssignmentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the shipmentAssignments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ShipmentAssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return shipmentAssignmentRepository.findAllWithEagerRelationships(pageable).map(shipmentAssignmentMapper::toDto);
    }

    /**
     * Get one shipmentAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShipmentAssignmentDTO> findOne(UUID id) {
        log.debug("Request to get ShipmentAssignment : {}", id);
        return shipmentAssignmentRepository.findOneWithEagerRelationships(id).map(shipmentAssignmentMapper::toDto);
    }

    /**
     * Delete the shipmentAssignment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete ShipmentAssignment : {}", id);
        shipmentAssignmentRepository.deleteById(id);
        shipmentAssignmentSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the shipmentAssignment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentAssignmentDTO> search(String query) {
        log.debug("Request to search ShipmentAssignments for query {}", query);
        try {
            return StreamSupport.stream(shipmentAssignmentSearchRepository.search(query).spliterator(), false)
                .map(shipmentAssignmentMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
