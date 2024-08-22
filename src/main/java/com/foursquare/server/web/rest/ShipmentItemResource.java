package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ShipmentItemRepository;
import com.foursquare.server.service.ShipmentItemQueryService;
import com.foursquare.server.service.ShipmentItemService;
import com.foursquare.server.service.criteria.ShipmentItemCriteria;
import com.foursquare.server.service.dto.ShipmentItemDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.ShipmentItem}.
 */
@RestController
@RequestMapping("/api/shipment-items")
public class ShipmentItemResource {

    private static final Logger log = LoggerFactory.getLogger(ShipmentItemResource.class);

    private static final String ENTITY_NAME = "shipmentItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShipmentItemService shipmentItemService;

    private final ShipmentItemRepository shipmentItemRepository;

    private final ShipmentItemQueryService shipmentItemQueryService;

    public ShipmentItemResource(
        ShipmentItemService shipmentItemService,
        ShipmentItemRepository shipmentItemRepository,
        ShipmentItemQueryService shipmentItemQueryService
    ) {
        this.shipmentItemService = shipmentItemService;
        this.shipmentItemRepository = shipmentItemRepository;
        this.shipmentItemQueryService = shipmentItemQueryService;
    }

    /**
     * {@code POST  /shipment-items} : Create a new shipmentItem.
     *
     * @param shipmentItemDTO the shipmentItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentItemDTO, or with status {@code 400 (Bad Request)} if the shipmentItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentItemDTO> createShipmentItem(@Valid @RequestBody ShipmentItemDTO shipmentItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save ShipmentItem : {}", shipmentItemDTO);
        if (shipmentItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipmentItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentItemDTO = shipmentItemService.save(shipmentItemDTO);
        return ResponseEntity.created(new URI("/api/shipment-items/" + shipmentItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, shipmentItemDTO.getId().toString()))
            .body(shipmentItemDTO);
    }

    /**
     * {@code PUT  /shipment-items/:id} : Updates an existing shipmentItem.
     *
     * @param id the id of the shipmentItemDTO to save.
     * @param shipmentItemDTO the shipmentItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentItemDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentItemDTO> updateShipmentItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ShipmentItemDTO shipmentItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ShipmentItem : {}, {}", id, shipmentItemDTO);
        if (shipmentItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentItemDTO = shipmentItemService.update(shipmentItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentItemDTO.getId().toString()))
            .body(shipmentItemDTO);
    }

    /**
     * {@code PATCH  /shipment-items/:id} : Partial updates given fields of an existing shipmentItem, field will ignore if it is null
     *
     * @param id the id of the shipmentItemDTO to save.
     * @param shipmentItemDTO the shipmentItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentItemDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentItemDTO> partialUpdateShipmentItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ShipmentItemDTO shipmentItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShipmentItem partially : {}, {}", id, shipmentItemDTO);
        if (shipmentItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentItemDTO> result = shipmentItemService.partialUpdate(shipmentItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shipmentItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipment-items} : get all the shipmentItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shipmentItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShipmentItemDTO>> getAllShipmentItems(ShipmentItemCriteria criteria) {
        log.debug("REST request to get ShipmentItems by criteria: {}", criteria);

        List<ShipmentItemDTO> entityList = shipmentItemQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /shipment-items/count} : count all the shipmentItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countShipmentItems(ShipmentItemCriteria criteria) {
        log.debug("REST request to count ShipmentItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(shipmentItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shipment-items/:id} : get the "id" shipmentItem.
     *
     * @param id the id of the shipmentItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentItemDTO> getShipmentItem(@PathVariable("id") UUID id) {
        log.debug("REST request to get ShipmentItem : {}", id);
        Optional<ShipmentItemDTO> shipmentItemDTO = shipmentItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentItemDTO);
    }

    /**
     * {@code DELETE  /shipment-items/:id} : delete the "id" shipmentItem.
     *
     * @param id the id of the shipmentItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentItem(@PathVariable("id") UUID id) {
        log.debug("REST request to delete ShipmentItem : {}", id);
        shipmentItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
