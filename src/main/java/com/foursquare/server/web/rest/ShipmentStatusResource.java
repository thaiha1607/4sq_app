package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ShipmentStatusRepository;
import com.foursquare.server.service.ShipmentStatusQueryService;
import com.foursquare.server.service.ShipmentStatusService;
import com.foursquare.server.service.criteria.ShipmentStatusCriteria;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import com.foursquare.server.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.foursquare.server.domain.ShipmentStatus}.
 */
@RestController
@RequestMapping("/api/shipment-statuses")
public class ShipmentStatusResource {

    private static final Logger log = LoggerFactory.getLogger(ShipmentStatusResource.class);

    private static final String ENTITY_NAME = "shipmentStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShipmentStatusService shipmentStatusService;

    private final ShipmentStatusRepository shipmentStatusRepository;

    private final ShipmentStatusQueryService shipmentStatusQueryService;

    public ShipmentStatusResource(
        ShipmentStatusService shipmentStatusService,
        ShipmentStatusRepository shipmentStatusRepository,
        ShipmentStatusQueryService shipmentStatusQueryService
    ) {
        this.shipmentStatusService = shipmentStatusService;
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.shipmentStatusQueryService = shipmentStatusQueryService;
    }

    /**
     * {@code POST  /shipment-statuses} : Create a new shipmentStatus.
     *
     * @param shipmentStatusDTO the shipmentStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentStatusDTO, or with status {@code 400 (Bad Request)} if the shipmentStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentStatusDTO> createShipmentStatus(@Valid @RequestBody ShipmentStatusDTO shipmentStatusDTO)
        throws URISyntaxException {
        log.debug("REST request to save ShipmentStatus : {}", shipmentStatusDTO);
        if (shipmentStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipmentStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentStatusDTO = shipmentStatusService.save(shipmentStatusDTO);
        return ResponseEntity.created(new URI("/api/shipment-statuses/" + shipmentStatusDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, shipmentStatusDTO.getId().toString()))
            .body(shipmentStatusDTO);
    }

    /**
     * {@code PUT  /shipment-statuses/:id} : Updates an existing shipmentStatus.
     *
     * @param id the id of the shipmentStatusDTO to save.
     * @param shipmentStatusDTO the shipmentStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentStatusDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentStatusDTO> updateShipmentStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShipmentStatusDTO shipmentStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ShipmentStatus : {}, {}", id, shipmentStatusDTO);
        if (shipmentStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentStatusDTO = shipmentStatusService.update(shipmentStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentStatusDTO.getId().toString()))
            .body(shipmentStatusDTO);
    }

    /**
     * {@code PATCH  /shipment-statuses/:id} : Partial updates given fields of an existing shipmentStatus, field will ignore if it is null
     *
     * @param id the id of the shipmentStatusDTO to save.
     * @param shipmentStatusDTO the shipmentStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentStatusDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentStatusDTO> partialUpdateShipmentStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShipmentStatusDTO shipmentStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShipmentStatus partially : {}, {}", id, shipmentStatusDTO);
        if (shipmentStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentStatusDTO> result = shipmentStatusService.partialUpdate(shipmentStatusDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentStatusDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipment-statuses} : get all the shipmentStatuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shipmentStatuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShipmentStatusDTO>> getAllShipmentStatuses(ShipmentStatusCriteria criteria) {
        log.debug("REST request to get ShipmentStatuses by criteria: {}", criteria);

        List<ShipmentStatusDTO> entityList = shipmentStatusQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /shipment-statuses/count} : count all the shipmentStatuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countShipmentStatuses(ShipmentStatusCriteria criteria) {
        log.debug("REST request to count ShipmentStatuses by criteria: {}", criteria);
        return ResponseEntity.ok().body(shipmentStatusQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shipment-statuses/:id} : get the "id" shipmentStatus.
     *
     * @param id the id of the shipmentStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentStatusDTO> getShipmentStatus(@PathVariable("id") Long id) {
        log.debug("REST request to get ShipmentStatus : {}", id);
        Optional<ShipmentStatusDTO> shipmentStatusDTO = shipmentStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentStatusDTO);
    }

    /**
     * {@code DELETE  /shipment-statuses/:id} : delete the "id" shipmentStatus.
     *
     * @param id the id of the shipmentStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentStatus(@PathVariable("id") Long id) {
        log.debug("REST request to delete ShipmentStatus : {}", id);
        shipmentStatusService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
