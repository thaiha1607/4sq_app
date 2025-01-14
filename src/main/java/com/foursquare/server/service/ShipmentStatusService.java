package com.foursquare.server.service;

import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.repository.ShipmentStatusRepository;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import com.foursquare.server.service.mapper.ShipmentStatusMapper;
import java.util.Optional;
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

    public ShipmentStatusService(ShipmentStatusRepository shipmentStatusRepository, ShipmentStatusMapper shipmentStatusMapper) {
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.shipmentStatusMapper = shipmentStatusMapper;
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
            .map(shipmentStatusMapper::toDto);
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
    }
}
