package com.foursquare.server.service;

import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.repository.ShipmentItemRepository;
import com.foursquare.server.repository.search.ShipmentItemSearchRepository;
import com.foursquare.server.service.dto.ShipmentItemDTO;
import com.foursquare.server.service.mapper.ShipmentItemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

    private final ShipmentItemSearchRepository shipmentItemSearchRepository;

    public ShipmentItemService(
        ShipmentItemRepository shipmentItemRepository,
        ShipmentItemMapper shipmentItemMapper,
        ShipmentItemSearchRepository shipmentItemSearchRepository
    ) {
        this.shipmentItemRepository = shipmentItemRepository;
        this.shipmentItemMapper = shipmentItemMapper;
        this.shipmentItemSearchRepository = shipmentItemSearchRepository;
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
        shipmentItemSearchRepository.index(shipmentItem);
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
        shipmentItemSearchRepository.index(shipmentItem);
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
            .map(savedShipmentItem -> {
                shipmentItemSearchRepository.index(savedShipmentItem);
                return savedShipmentItem;
            })
            .map(shipmentItemMapper::toDto);
    }

    /**
     * Get all the shipmentItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentItemDTO> findAll() {
        log.debug("Request to get all ShipmentItems");
        return shipmentItemRepository.findAll().stream().map(shipmentItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
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
        shipmentItemSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the shipmentItem corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ShipmentItemDTO> search(String query) {
        log.debug("Request to search ShipmentItems for query {}", query);
        try {
            return StreamSupport.stream(shipmentItemSearchRepository.search(query).spliterator(), false)
                .map(shipmentItemMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
