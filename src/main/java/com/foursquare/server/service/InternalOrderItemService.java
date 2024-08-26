package com.foursquare.server.service;

import com.foursquare.server.domain.InternalOrderItem;
import com.foursquare.server.repository.InternalOrderItemRepository;
import com.foursquare.server.service.dto.InternalOrderItemDTO;
import com.foursquare.server.service.mapper.InternalOrderItemMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.InternalOrderItem}.
 */
@Service
@Transactional
public class InternalOrderItemService {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderItemService.class);

    private final InternalOrderItemRepository internalOrderItemRepository;

    private final InternalOrderItemMapper internalOrderItemMapper;

    public InternalOrderItemService(
        InternalOrderItemRepository internalOrderItemRepository,
        InternalOrderItemMapper internalOrderItemMapper
    ) {
        this.internalOrderItemRepository = internalOrderItemRepository;
        this.internalOrderItemMapper = internalOrderItemMapper;
    }

    /**
     * Save a internalOrderItem.
     *
     * @param internalOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public InternalOrderItemDTO save(InternalOrderItemDTO internalOrderItemDTO) {
        log.debug("Request to save InternalOrderItem : {}", internalOrderItemDTO);
        InternalOrderItem internalOrderItem = internalOrderItemMapper.toEntity(internalOrderItemDTO);
        internalOrderItem = internalOrderItemRepository.save(internalOrderItem);
        return internalOrderItemMapper.toDto(internalOrderItem);
    }

    /**
     * Update a internalOrderItem.
     *
     * @param internalOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public InternalOrderItemDTO update(InternalOrderItemDTO internalOrderItemDTO) {
        log.debug("Request to update InternalOrderItem : {}", internalOrderItemDTO);
        InternalOrderItem internalOrderItem = internalOrderItemMapper.toEntity(internalOrderItemDTO);
        internalOrderItem.setIsPersisted();
        internalOrderItem = internalOrderItemRepository.save(internalOrderItem);
        return internalOrderItemMapper.toDto(internalOrderItem);
    }

    /**
     * Partially update a internalOrderItem.
     *
     * @param internalOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InternalOrderItemDTO> partialUpdate(InternalOrderItemDTO internalOrderItemDTO) {
        log.debug("Request to partially update InternalOrderItem : {}", internalOrderItemDTO);

        return internalOrderItemRepository
            .findById(internalOrderItemDTO.getId())
            .map(existingInternalOrderItem -> {
                internalOrderItemMapper.partialUpdate(existingInternalOrderItem, internalOrderItemDTO);

                return existingInternalOrderItem;
            })
            .map(internalOrderItemRepository::save)
            .map(internalOrderItemMapper::toDto);
    }

    /**
     * Get one internalOrderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InternalOrderItemDTO> findOne(UUID id) {
        log.debug("Request to get InternalOrderItem : {}", id);
        return internalOrderItemRepository.findById(id).map(internalOrderItemMapper::toDto);
    }

    /**
     * Delete the internalOrderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete InternalOrderItem : {}", id);
        internalOrderItemRepository.deleteById(id);
    }
}
