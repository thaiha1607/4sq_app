package com.foursquare.server.service;

import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.repository.ShipmentAssignmentRepository;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.service.mapper.ShipmentAssignmentMapper;
import java.util.Optional;
import java.util.UUID;
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

    public ShipmentAssignmentService(
        ShipmentAssignmentRepository shipmentAssignmentRepository,
        ShipmentAssignmentMapper shipmentAssignmentMapper
    ) {
        this.shipmentAssignmentRepository = shipmentAssignmentRepository;
        this.shipmentAssignmentMapper = shipmentAssignmentMapper;
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
            .map(shipmentAssignmentMapper::toDto);
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
    }
}
