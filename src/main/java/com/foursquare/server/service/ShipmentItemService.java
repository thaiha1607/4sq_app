package com.foursquare.server.service;

import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.repository.ShipmentItemRepository;
import com.foursquare.server.service.dto.ShipmentItemDTO;
import com.foursquare.server.service.mapper.ShipmentItemMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.ShipmentItem}.
 */
@Service
@Transactional
public class ShipmentItemService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentItemService.class);

    private final ShipmentItemRepository shipmentItemRepository;

    private final ShipmentItemMapper shipmentItemMapper;

    public ShipmentItemService(ShipmentItemRepository shipmentItemRepository, ShipmentItemMapper shipmentItemMapper) {
        this.shipmentItemRepository = shipmentItemRepository;
        this.shipmentItemMapper = shipmentItemMapper;
    }

    /**
     * Save a shipmentItem.
     *
     * @param shipmentItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentItemDTO save(ShipmentItemDTO shipmentItemDTO) {
        log.debug("Request to save ShipmentItem : {}", shipmentItemDTO);
        ShipmentItem shipmentItem = shipmentItemMapper.toEntity(shipmentItemDTO);
        shipmentItem = shipmentItemRepository.save(shipmentItem);
        return shipmentItemMapper.toDto(shipmentItem);
    }

    /**
     * Update a shipmentItem.
     *
     * @param shipmentItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentItemDTO update(ShipmentItemDTO shipmentItemDTO) {
        log.debug("Request to update ShipmentItem : {}", shipmentItemDTO);
        ShipmentItem shipmentItem = shipmentItemMapper.toEntity(shipmentItemDTO);
        shipmentItem.setIsPersisted();
        shipmentItem = shipmentItemRepository.save(shipmentItem);
        return shipmentItemMapper.toDto(shipmentItem);
    }

    /**
     * Partially update a shipmentItem.
     *
     * @param shipmentItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ShipmentItemDTO> partialUpdate(ShipmentItemDTO shipmentItemDTO) {
        log.debug("Request to partially update ShipmentItem : {}", shipmentItemDTO);

        return shipmentItemRepository
            .findById(shipmentItemDTO.getId())
            .map(existingShipmentItem -> {
                shipmentItemMapper.partialUpdate(existingShipmentItem, shipmentItemDTO);

                return existingShipmentItem;
            })
            .map(shipmentItemRepository::save)
            .map(shipmentItemMapper::toDto);
    }

    /**
     * Get one shipmentItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShipmentItemDTO> findOne(UUID id) {
        log.debug("Request to get ShipmentItem : {}", id);
        return shipmentItemRepository.findById(id).map(shipmentItemMapper::toDto);
    }

    /**
     * Delete the shipmentItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete ShipmentItem : {}", id);
        shipmentItemRepository.deleteById(id);
    }
}
