package com.foursquare.server.web.rest;

import com.foursquare.server.repository.WarehouseAssignmentRepository;
import com.foursquare.server.service.WarehouseAssignmentQueryService;
import com.foursquare.server.service.WarehouseAssignmentService;
import com.foursquare.server.service.criteria.WarehouseAssignmentCriteria;
import com.foursquare.server.service.dto.WarehouseAssignmentDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.WarehouseAssignment}.
 */
@RestController
@RequestMapping("/api/warehouse-assignments")
public class WarehouseAssignmentResource {

    private static final Logger log = LoggerFactory.getLogger(WarehouseAssignmentResource.class);

    private static final String ENTITY_NAME = "warehouseAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WarehouseAssignmentService warehouseAssignmentService;

    private final WarehouseAssignmentRepository warehouseAssignmentRepository;

    private final WarehouseAssignmentQueryService warehouseAssignmentQueryService;

    public WarehouseAssignmentResource(
        WarehouseAssignmentService warehouseAssignmentService,
        WarehouseAssignmentRepository warehouseAssignmentRepository,
        WarehouseAssignmentQueryService warehouseAssignmentQueryService
    ) {
        this.warehouseAssignmentService = warehouseAssignmentService;
        this.warehouseAssignmentRepository = warehouseAssignmentRepository;
        this.warehouseAssignmentQueryService = warehouseAssignmentQueryService;
    }

    /**
     * {@code POST  /warehouse-assignments} : Create a new warehouseAssignment.
     *
     * @param warehouseAssignmentDTO the warehouseAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new warehouseAssignmentDTO, or with status {@code 400 (Bad Request)} if the warehouseAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WarehouseAssignmentDTO> createWarehouseAssignment(
        @Valid @RequestBody WarehouseAssignmentDTO warehouseAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to save WarehouseAssignment : {}", warehouseAssignmentDTO);
        if (warehouseAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new warehouseAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        warehouseAssignmentDTO = warehouseAssignmentService.save(warehouseAssignmentDTO);
        return ResponseEntity.created(new URI("/api/warehouse-assignments/" + warehouseAssignmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, warehouseAssignmentDTO.getId().toString()))
            .body(warehouseAssignmentDTO);
    }

    /**
     * {@code PUT  /warehouse-assignments/:id} : Updates an existing warehouseAssignment.
     *
     * @param id the id of the warehouseAssignmentDTO to save.
     * @param warehouseAssignmentDTO the warehouseAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the warehouseAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseAssignmentDTO> updateWarehouseAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody WarehouseAssignmentDTO warehouseAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update WarehouseAssignment : {}, {}", id, warehouseAssignmentDTO);
        if (warehouseAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, warehouseAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!warehouseAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        warehouseAssignmentDTO = warehouseAssignmentService.update(warehouseAssignmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, warehouseAssignmentDTO.getId().toString()))
            .body(warehouseAssignmentDTO);
    }

    /**
     * {@code PATCH  /warehouse-assignments/:id} : Partial updates given fields of an existing warehouseAssignment, field will ignore if it is null
     *
     * @param id the id of the warehouseAssignmentDTO to save.
     * @param warehouseAssignmentDTO the warehouseAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated warehouseAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the warehouseAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the warehouseAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the warehouseAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WarehouseAssignmentDTO> partialUpdateWarehouseAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody WarehouseAssignmentDTO warehouseAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update WarehouseAssignment partially : {}, {}", id, warehouseAssignmentDTO);
        if (warehouseAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, warehouseAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!warehouseAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WarehouseAssignmentDTO> result = warehouseAssignmentService.partialUpdate(warehouseAssignmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, warehouseAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /warehouse-assignments} : get all the warehouseAssignments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of warehouseAssignments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WarehouseAssignmentDTO>> getAllWarehouseAssignments(WarehouseAssignmentCriteria criteria) {
        log.debug("REST request to get WarehouseAssignments by criteria: {}", criteria);

        List<WarehouseAssignmentDTO> entityList = warehouseAssignmentQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /warehouse-assignments/count} : count all the warehouseAssignments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWarehouseAssignments(WarehouseAssignmentCriteria criteria) {
        log.debug("REST request to count WarehouseAssignments by criteria: {}", criteria);
        return ResponseEntity.ok().body(warehouseAssignmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /warehouse-assignments/:id} : get the "id" warehouseAssignment.
     *
     * @param id the id of the warehouseAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the warehouseAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseAssignmentDTO> getWarehouseAssignment(@PathVariable("id") UUID id) {
        log.debug("REST request to get WarehouseAssignment : {}", id);
        Optional<WarehouseAssignmentDTO> warehouseAssignmentDTO = warehouseAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(warehouseAssignmentDTO);
    }

    /**
     * {@code DELETE  /warehouse-assignments/:id} : delete the "id" warehouseAssignment.
     *
     * @param id the id of the warehouseAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouseAssignment(@PathVariable("id") UUID id) {
        log.debug("REST request to delete WarehouseAssignment : {}", id);
        warehouseAssignmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
