package com.foursquare.server.service;

import com.foursquare.server.domain.InternalOrder;
import com.foursquare.server.repository.InternalOrderRepository;
import com.foursquare.server.service.dto.InternalOrderDTO;
import com.foursquare.server.service.mapper.InternalOrderMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.InternalOrder}.
 */
@Service
@Transactional
public class InternalOrderService {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderService.class);

    private final InternalOrderRepository internalOrderRepository;

    private final InternalOrderMapper internalOrderMapper;

    public InternalOrderService(InternalOrderRepository internalOrderRepository, InternalOrderMapper internalOrderMapper) {
        this.internalOrderRepository = internalOrderRepository;
        this.internalOrderMapper = internalOrderMapper;
    }

    /**
     * Save a internalOrder.
     *
     * @param internalOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public InternalOrderDTO save(InternalOrderDTO internalOrderDTO) {
        log.debug("Request to save InternalOrder : {}", internalOrderDTO);
        InternalOrder internalOrder = internalOrderMapper.toEntity(internalOrderDTO);
        internalOrder = internalOrderRepository.save(internalOrder);
        return internalOrderMapper.toDto(internalOrder);
    }

    /**
     * Update a internalOrder.
     *
     * @param internalOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public InternalOrderDTO update(InternalOrderDTO internalOrderDTO) {
        log.debug("Request to update InternalOrder : {}", internalOrderDTO);
        InternalOrder internalOrder = internalOrderMapper.toEntity(internalOrderDTO);
        internalOrder.setIsPersisted();
        internalOrder = internalOrderRepository.save(internalOrder);
        return internalOrderMapper.toDto(internalOrder);
    }

    /**
     * Partially update a internalOrder.
     *
     * @param internalOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InternalOrderDTO> partialUpdate(InternalOrderDTO internalOrderDTO) {
        log.debug("Request to partially update InternalOrder : {}", internalOrderDTO);

        return internalOrderRepository
            .findById(internalOrderDTO.getId())
            .map(existingInternalOrder -> {
                internalOrderMapper.partialUpdate(existingInternalOrder, internalOrderDTO);

                return existingInternalOrder;
            })
            .map(internalOrderRepository::save)
            .map(internalOrderMapper::toDto);
    }

    /**
     * Get all the internalOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InternalOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return internalOrderRepository.findAllWithEagerRelationships(pageable).map(internalOrderMapper::toDto);
    }

    /**
     * Get one internalOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InternalOrderDTO> findOne(UUID id) {
        log.debug("Request to get InternalOrder : {}", id);
        return internalOrderRepository.findOneWithEagerRelationships(id).map(internalOrderMapper::toDto);
    }

    /**
     * Delete the internalOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete InternalOrder : {}", id);
        internalOrderRepository.deleteById(id);
    }
}
