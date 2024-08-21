package com.foursquare.server.service;

import com.foursquare.server.domain.WarehouseAssignment;
import com.foursquare.server.repository.WarehouseAssignmentRepository;
import com.foursquare.server.service.dto.WarehouseAssignmentDTO;
import com.foursquare.server.service.mapper.WarehouseAssignmentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.WarehouseAssignment}.
 */
@Service
@Transactional
public class WarehouseAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseAssignmentService.class);

    private final WarehouseAssignmentRepository warehouseAssignmentRepository;

    private final WarehouseAssignmentMapper warehouseAssignmentMapper;

    public WarehouseAssignmentService(
        WarehouseAssignmentRepository warehouseAssignmentRepository,
        WarehouseAssignmentMapper warehouseAssignmentMapper
    ) {
        this.warehouseAssignmentRepository = warehouseAssignmentRepository;
        this.warehouseAssignmentMapper = warehouseAssignmentMapper;
    }

    /**
     * Save a warehouseAssignment.
     *
     * @param warehouseAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseAssignmentDTO save(WarehouseAssignmentDTO warehouseAssignmentDTO) {
        log.debug("Request to save WarehouseAssignment : {}", warehouseAssignmentDTO);
        WarehouseAssignment warehouseAssignment = warehouseAssignmentMapper.toEntity(warehouseAssignmentDTO);
        warehouseAssignment = warehouseAssignmentRepository.save(warehouseAssignment);
        return warehouseAssignmentMapper.toDto(warehouseAssignment);
    }

    /**
     * Update a warehouseAssignment.
     *
     * @param warehouseAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public WarehouseAssignmentDTO update(WarehouseAssignmentDTO warehouseAssignmentDTO) {
        log.debug("Request to update WarehouseAssignment : {}", warehouseAssignmentDTO);
        WarehouseAssignment warehouseAssignment = warehouseAssignmentMapper.toEntity(warehouseAssignmentDTO);
        warehouseAssignment.setIsPersisted();
        warehouseAssignment = warehouseAssignmentRepository.save(warehouseAssignment);
        return warehouseAssignmentMapper.toDto(warehouseAssignment);
    }

    /**
     * Partially update a warehouseAssignment.
     *
     * @param warehouseAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WarehouseAssignmentDTO> partialUpdate(WarehouseAssignmentDTO warehouseAssignmentDTO) {
        log.debug("Request to partially update WarehouseAssignment : {}", warehouseAssignmentDTO);

        return warehouseAssignmentRepository
            .findById(warehouseAssignmentDTO.getId())
            .map(existingWarehouseAssignment -> {
                warehouseAssignmentMapper.partialUpdate(existingWarehouseAssignment, warehouseAssignmentDTO);

                return existingWarehouseAssignment;
            })
            .map(warehouseAssignmentRepository::save)
            .map(warehouseAssignmentMapper::toDto);
    }

    /**
     * Get all the warehouseAssignments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseAssignmentDTO> findAll() {
        log.debug("Request to get all WarehouseAssignments");
        return warehouseAssignmentRepository
            .findAll()
            .stream()
            .map(warehouseAssignmentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the warehouseAssignments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WarehouseAssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return warehouseAssignmentRepository.findAllWithEagerRelationships(pageable).map(warehouseAssignmentMapper::toDto);
    }

    /**
     * Get one warehouseAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseAssignmentDTO> findOne(UUID id) {
        log.debug("Request to get WarehouseAssignment : {}", id);
        return warehouseAssignmentRepository.findOneWithEagerRelationships(id).map(warehouseAssignmentMapper::toDto);
    }

    /**
     * Delete the warehouseAssignment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete WarehouseAssignment : {}", id);
        warehouseAssignmentRepository.deleteById(id);
    }
}
