package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ShipmentAssignmentRepository;
import com.foursquare.server.service.ShipmentAssignmentService;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.web.rest.errors.BadRequestAlertException;
import com.foursquare.server.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link com.foursquare.server.domain.ShipmentAssignment}.
 */
@RestController
@RequestMapping("/api/shipment-assignments")
public class ShipmentAssignmentResource {

    private static final Logger log = LoggerFactory.getLogger(ShipmentAssignmentResource.class);

    private static final String ENTITY_NAME = "shipmentAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShipmentAssignmentService shipmentAssignmentService;

    private final ShipmentAssignmentRepository shipmentAssignmentRepository;

    public ShipmentAssignmentResource(
        ShipmentAssignmentService shipmentAssignmentService,
        ShipmentAssignmentRepository shipmentAssignmentRepository
    ) {
        this.shipmentAssignmentService = shipmentAssignmentService;
        this.shipmentAssignmentRepository = shipmentAssignmentRepository;
    }

    /**
     * {@code POST  /shipment-assignments} : Create a new shipmentAssignment.
     *
     * @param shipmentAssignmentDTO the shipmentAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentAssignmentDTO, or with status {@code 400 (Bad Request)} if the shipmentAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentAssignmentDTO> createShipmentAssignment(@Valid @RequestBody ShipmentAssignmentDTO shipmentAssignmentDTO)
        throws URISyntaxException {
        log.debug("REST request to save ShipmentAssignment : {}", shipmentAssignmentDTO);
        if (shipmentAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipmentAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentAssignmentDTO = shipmentAssignmentService.save(shipmentAssignmentDTO);
        return ResponseEntity.created(new URI("/api/shipment-assignments/" + shipmentAssignmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, shipmentAssignmentDTO.getId().toString()))
            .body(shipmentAssignmentDTO);
    }

    /**
     * {@code PUT  /shipment-assignments/:id} : Updates an existing shipmentAssignment.
     *
     * @param id the id of the shipmentAssignmentDTO to save.
     * @param shipmentAssignmentDTO the shipmentAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentAssignmentDTO> updateShipmentAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ShipmentAssignmentDTO shipmentAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ShipmentAssignment : {}, {}", id, shipmentAssignmentDTO);
        if (shipmentAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentAssignmentDTO = shipmentAssignmentService.update(shipmentAssignmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentAssignmentDTO.getId().toString()))
            .body(shipmentAssignmentDTO);
    }

    /**
     * {@code PATCH  /shipment-assignments/:id} : Partial updates given fields of an existing shipmentAssignment, field will ignore if it is null
     *
     * @param id the id of the shipmentAssignmentDTO to save.
     * @param shipmentAssignmentDTO the shipmentAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentAssignmentDTO> partialUpdateShipmentAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ShipmentAssignmentDTO shipmentAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShipmentAssignment partially : {}, {}", id, shipmentAssignmentDTO);
        if (shipmentAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentAssignmentDTO> result = shipmentAssignmentService.partialUpdate(shipmentAssignmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipment-assignments} : get all the shipmentAssignments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shipmentAssignments in body.
     */
    @GetMapping("")
    public List<ShipmentAssignmentDTO> getAllShipmentAssignments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all ShipmentAssignments");
        return shipmentAssignmentService.findAll();
    }

    /**
     * {@code GET  /shipment-assignments/:id} : get the "id" shipmentAssignment.
     *
     * @param id the id of the shipmentAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentAssignmentDTO> getShipmentAssignment(@PathVariable("id") UUID id) {
        log.debug("REST request to get ShipmentAssignment : {}", id);
        Optional<ShipmentAssignmentDTO> shipmentAssignmentDTO = shipmentAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentAssignmentDTO);
    }

    /**
     * {@code DELETE  /shipment-assignments/:id} : delete the "id" shipmentAssignment.
     *
     * @param id the id of the shipmentAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentAssignment(@PathVariable("id") UUID id) {
        log.debug("REST request to delete ShipmentAssignment : {}", id);
        shipmentAssignmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /shipment-assignments/_search?query=:query} : search for the shipmentAssignment corresponding
     * to the query.
     *
     * @param query the query of the shipmentAssignment search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ShipmentAssignmentDTO> searchShipmentAssignments(@RequestParam("query") String query) {
        log.debug("REST request to search ShipmentAssignments for query {}", query);
        try {
            return shipmentAssignmentService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
