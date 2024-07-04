package com.foursquare.server.service;

import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.repository.WorkingUnitRepository;
import com.foursquare.server.repository.search.WorkingUnitSearchRepository;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import com.foursquare.server.service.mapper.WorkingUnitMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.WorkingUnit}.
 */
@Service
@Transactional
public class WorkingUnitService {

    private static final Logger log = LoggerFactory.getLogger(WorkingUnitService.class);

    private final WorkingUnitRepository workingUnitRepository;

    private final WorkingUnitMapper workingUnitMapper;

    private final WorkingUnitSearchRepository workingUnitSearchRepository;

    public WorkingUnitService(
        WorkingUnitRepository workingUnitRepository,
        WorkingUnitMapper workingUnitMapper,
        WorkingUnitSearchRepository workingUnitSearchRepository
    ) {
        this.workingUnitRepository = workingUnitRepository;
        this.workingUnitMapper = workingUnitMapper;
        this.workingUnitSearchRepository = workingUnitSearchRepository;
    }

    /**
     * Save a workingUnit.
     *
     * @param workingUnitDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkingUnitDTO save(WorkingUnitDTO workingUnitDTO) {
        log.debug("Request to save WorkingUnit : {}", workingUnitDTO);
        WorkingUnit workingUnit = workingUnitMapper.toEntity(workingUnitDTO);
        workingUnit = workingUnitRepository.save(workingUnit);
        workingUnitSearchRepository.index(workingUnit);
        return workingUnitMapper.toDto(workingUnit);
    }

    /**
     * Update a workingUnit.
     *
     * @param workingUnitDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkingUnitDTO update(WorkingUnitDTO workingUnitDTO) {
        log.debug("Request to update WorkingUnit : {}", workingUnitDTO);
        WorkingUnit workingUnit = workingUnitMapper.toEntity(workingUnitDTO);
        workingUnit.setIsPersisted();
        workingUnit = workingUnitRepository.save(workingUnit);
        workingUnitSearchRepository.index(workingUnit);
        return workingUnitMapper.toDto(workingUnit);
    }

    /**
     * Partially update a workingUnit.
     *
     * @param workingUnitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkingUnitDTO> partialUpdate(WorkingUnitDTO workingUnitDTO) {
        log.debug("Request to partially update WorkingUnit : {}", workingUnitDTO);

        return workingUnitRepository
            .findById(workingUnitDTO.getId())
            .map(existingWorkingUnit -> {
                workingUnitMapper.partialUpdate(existingWorkingUnit, workingUnitDTO);

                return existingWorkingUnit;
            })
            .map(workingUnitRepository::save)
            .map(savedWorkingUnit -> {
                workingUnitSearchRepository.index(savedWorkingUnit);
                return savedWorkingUnit;
            })
            .map(workingUnitMapper::toDto);
    }

    /**
     * Get all the workingUnits.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WorkingUnitDTO> findAll() {
        log.debug("Request to get all WorkingUnits");
        return workingUnitRepository.findAll().stream().map(workingUnitMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one workingUnit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkingUnitDTO> findOne(UUID id) {
        log.debug("Request to get WorkingUnit : {}", id);
        return workingUnitRepository.findById(id).map(workingUnitMapper::toDto);
    }

    /**
     * Delete the workingUnit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete WorkingUnit : {}", id);
        workingUnitRepository.deleteById(id);
        workingUnitSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the workingUnit corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WorkingUnitDTO> search(String query) {
        log.debug("Request to search WorkingUnits for query {}", query);
        try {
            return StreamSupport.stream(workingUnitSearchRepository.search(query).spliterator(), false)
                .map(workingUnitMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
