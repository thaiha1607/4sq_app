package com.foursquare.server.web.rest;

import com.foursquare.server.repository.WorkingUnitRepository;
import com.foursquare.server.service.WorkingUnitQueryService;
import com.foursquare.server.service.WorkingUnitService;
import com.foursquare.server.service.criteria.WorkingUnitCriteria;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import com.foursquare.server.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.foursquare.server.domain.WorkingUnit}.
 */
@RestController
@RequestMapping("/api/working-units")
public class WorkingUnitResource {

    private static final Logger log = LoggerFactory.getLogger(WorkingUnitResource.class);

    private static final String ENTITY_NAME = "workingUnit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkingUnitService workingUnitService;

    private final WorkingUnitRepository workingUnitRepository;

    private final WorkingUnitQueryService workingUnitQueryService;

    public WorkingUnitResource(
        WorkingUnitService workingUnitService,
        WorkingUnitRepository workingUnitRepository,
        WorkingUnitQueryService workingUnitQueryService
    ) {
        this.workingUnitService = workingUnitService;
        this.workingUnitRepository = workingUnitRepository;
        this.workingUnitQueryService = workingUnitQueryService;
    }

    /**
     * {@code POST  /working-units} : Create a new workingUnit.
     *
     * @param workingUnitDTO the workingUnitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workingUnitDTO, or with status {@code 400 (Bad Request)} if the workingUnit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WorkingUnitDTO> createWorkingUnit(@Valid @RequestBody WorkingUnitDTO workingUnitDTO) throws URISyntaxException {
        log.debug("REST request to save WorkingUnit : {}", workingUnitDTO);
        if (workingUnitDTO.getId() != null) {
            throw new BadRequestAlertException("A new workingUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        workingUnitDTO = workingUnitService.save(workingUnitDTO);
        return ResponseEntity.created(new URI("/api/working-units/" + workingUnitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, workingUnitDTO.getId().toString()))
            .body(workingUnitDTO);
    }

    /**
     * {@code PUT  /working-units/:id} : Updates an existing workingUnit.
     *
     * @param id the id of the workingUnitDTO to save.
     * @param workingUnitDTO the workingUnitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workingUnitDTO,
     * or with status {@code 400 (Bad Request)} if the workingUnitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workingUnitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkingUnitDTO> updateWorkingUnit(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody WorkingUnitDTO workingUnitDTO
    ) throws URISyntaxException {
        log.debug("REST request to update WorkingUnit : {}, {}", id, workingUnitDTO);
        if (workingUnitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workingUnitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workingUnitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        workingUnitDTO = workingUnitService.update(workingUnitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workingUnitDTO.getId().toString()))
            .body(workingUnitDTO);
    }

    /**
     * {@code PATCH  /working-units/:id} : Partial updates given fields of an existing workingUnit, field will ignore if it is null
     *
     * @param id the id of the workingUnitDTO to save.
     * @param workingUnitDTO the workingUnitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workingUnitDTO,
     * or with status {@code 400 (Bad Request)} if the workingUnitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workingUnitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workingUnitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkingUnitDTO> partialUpdateWorkingUnit(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody WorkingUnitDTO workingUnitDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkingUnit partially : {}, {}", id, workingUnitDTO);
        if (workingUnitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workingUnitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workingUnitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkingUnitDTO> result = workingUnitService.partialUpdate(workingUnitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workingUnitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /working-units} : get all the workingUnits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workingUnits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WorkingUnitDTO>> getAllWorkingUnits(WorkingUnitCriteria criteria) {
        log.debug("REST request to get WorkingUnits by criteria: {}", criteria);

        List<WorkingUnitDTO> entityList = workingUnitQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /working-units/count} : count all the workingUnits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWorkingUnits(WorkingUnitCriteria criteria) {
        log.debug("REST request to count WorkingUnits by criteria: {}", criteria);
        return ResponseEntity.ok().body(workingUnitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /working-units/:id} : get the "id" workingUnit.
     *
     * @param id the id of the workingUnitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workingUnitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkingUnitDTO> getWorkingUnit(@PathVariable("id") UUID id) {
        log.debug("REST request to get WorkingUnit : {}", id);
        Optional<WorkingUnitDTO> workingUnitDTO = workingUnitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workingUnitDTO);
    }

    /**
     * {@code DELETE  /working-units/:id} : delete the "id" workingUnit.
     *
     * @param id the id of the workingUnitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkingUnit(@PathVariable("id") UUID id) {
        log.debug("REST request to delete WorkingUnit : {}", id);
        workingUnitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
