package com.foursquare.server.service;

import com.foursquare.server.domain.Shipment;
import com.foursquare.server.repository.ShipmentRepository;
import com.foursquare.server.repository.search.ShipmentSearchRepository;
import com.foursquare.server.service.dto.ShipmentDTO;
import com.foursquare.server.service.mapper.ShipmentMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.Shipment}.
 */
@Service
@Transactional
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);

    private final ShipmentRepository shipmentRepository;

    private final ShipmentMapper shipmentMapper;

    private final ShipmentSearchRepository shipmentSearchRepository;

    public ShipmentService(
        ShipmentRepository shipmentRepository,
        ShipmentMapper shipmentMapper,
        ShipmentSearchRepository shipmentSearchRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
        this.shipmentSearchRepository = shipmentSearchRepository;
    }

    /**
     * Save a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentDTO save(ShipmentDTO shipmentDTO) {
        log.debug("Request to save Shipment : {}", shipmentDTO);
        Shipment shipment = shipmentMapper.toEntity(shipmentDTO);
        shipment = shipmentRepository.save(shipment);
        shipmentSearchRepository.index(shipment);
        return shipmentMapper.toDto(shipment);
    }

    /**
     * Update a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentDTO update(ShipmentDTO shipmentDTO) {
        log.debug("Request to update Shipment : {}", shipmentDTO);
        Shipment shipment = shipmentMapper.toEntity(shipmentDTO);
        shipment.setIsPersisted();
        shipment = shipmentRepository.save(shipment);
        shipmentSearchRepository.index(shipment);
        return shipmentMapper.toDto(shipment);
    }

    /**
     * Partially update a shipment.
     *
     * @param shipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ShipmentDTO> partialUpdate(ShipmentDTO shipmentDTO) {
        log.debug("Request to partially update Shipment : {}", shipmentDTO);

        return shipmentRepository
            .findById(shipmentDTO.getId())
            .map(existingShipment -> {
                shipmentMapper.partialUpdate(existingShipment, shipmentDTO);

                return existingShipment;
            })
            .map(shipmentRepository::save)
            .map(savedShipment -> {
                shipmentSearchRepository.index(savedShipment);
                return savedShipment;
            })
            .map(shipmentMapper::toDto);
    }

    /**
     * Get all the shipments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentDTO> findAll() {
        log.debug("Request to get all Shipments");
        return shipmentRepository.findAll().stream().map(shipmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the shipments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ShipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return shipmentRepository.findAllWithEagerRelationships(pageable).map(shipmentMapper::toDto);
    }

    /**
     * Get one shipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShipmentDTO> findOne(UUID id) {
        log.debug("Request to get Shipment : {}", id);
        return shipmentRepository.findOneWithEagerRelationships(id).map(shipmentMapper::toDto);
    }

    /**
     * Delete the shipment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Shipment : {}", id);
        shipmentRepository.deleteById(id);
        shipmentSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the shipment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentDTO> search(String query) {
        log.debug("Request to search Shipments for query {}", query);
        try {
            return StreamSupport.stream(shipmentSearchRepository.search(query).spliterator(), false).map(shipmentMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
