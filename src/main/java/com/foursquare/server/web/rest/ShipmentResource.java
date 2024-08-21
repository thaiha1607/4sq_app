package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ShipmentRepository;
import com.foursquare.server.service.ShipmentService;
import com.foursquare.server.service.dto.ShipmentDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.Shipment}.
 */
@RestController
@RequestMapping("/api/shipments")
public class ShipmentResource {

    private static final Logger log = LoggerFactory.getLogger(ShipmentResource.class);

    private static final String ENTITY_NAME = "shipment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShipmentService shipmentService;

    private final ShipmentRepository shipmentRepository;

    public ShipmentResource(ShipmentService shipmentService, ShipmentRepository shipmentRepository) {
        this.shipmentService = shipmentService;
        this.shipmentRepository = shipmentRepository;
    }

    /**
     * {@code POST  /shipments} : Create a new shipment.
     *
     * @param shipmentDTO the shipmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentDTO, or with status {@code 400 (Bad Request)} if the shipment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentDTO> createShipment(@Valid @RequestBody ShipmentDTO shipmentDTO) throws URISyntaxException {
        log.debug("REST request to save Shipment : {}", shipmentDTO);
        if (shipmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentDTO = shipmentService.save(shipmentDTO);
        return ResponseEntity.created(new URI("/api/shipments/" + shipmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, shipmentDTO.getId().toString()))
            .body(shipmentDTO);
    }

    /**
     * {@code PUT  /shipments/:id} : Updates an existing shipment.
     *
     * @param id the id of the shipmentDTO to save.
     * @param shipmentDTO the shipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentDTO> updateShipment(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ShipmentDTO shipmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Shipment : {}, {}", id, shipmentDTO);
        if (shipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentDTO = shipmentService.update(shipmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentDTO.getId().toString()))
            .body(shipmentDTO);
    }

    /**
     * {@code PATCH  /shipments/:id} : Partial updates given fields of an existing shipment, field will ignore if it is null
     *
     * @param id the id of the shipmentDTO to save.
     * @param shipmentDTO the shipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentDTO> partialUpdateShipment(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ShipmentDTO shipmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Shipment partially : {}, {}", id, shipmentDTO);
        if (shipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentDTO> result = shipmentService.partialUpdate(shipmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipments} : get all the shipments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shipments in body.
     */
    @GetMapping("")
    public List<ShipmentDTO> getAllShipments(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Shipments");
        return shipmentService.findAll();
    }

    /**
     * {@code GET  /shipments/:id} : get the "id" shipment.
     *
     * @param id the id of the shipmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipment(@PathVariable("id") UUID id) {
        log.debug("REST request to get Shipment : {}", id);
        Optional<ShipmentDTO> shipmentDTO = shipmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentDTO);
    }

    /**
     * {@code DELETE  /shipments/:id} : delete the "id" shipment.
     *
     * @param id the id of the shipmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable("id") UUID id) {
        log.debug("REST request to delete Shipment : {}", id);
        shipmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
