package com.foursquare.server.web.rest;

import com.foursquare.server.repository.InternalOrderItemRepository;
import com.foursquare.server.service.InternalOrderItemQueryService;
import com.foursquare.server.service.InternalOrderItemService;
import com.foursquare.server.service.criteria.InternalOrderItemCriteria;
import com.foursquare.server.service.dto.InternalOrderItemDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.InternalOrderItem}.
 */
@RestController
@RequestMapping("/api/internal-order-items")
public class InternalOrderItemResource {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderItemResource.class);

    private static final String ENTITY_NAME = "internalOrderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalOrderItemService internalOrderItemService;

    private final InternalOrderItemRepository internalOrderItemRepository;

    private final InternalOrderItemQueryService internalOrderItemQueryService;

    public InternalOrderItemResource(
        InternalOrderItemService internalOrderItemService,
        InternalOrderItemRepository internalOrderItemRepository,
        InternalOrderItemQueryService internalOrderItemQueryService
    ) {
        this.internalOrderItemService = internalOrderItemService;
        this.internalOrderItemRepository = internalOrderItemRepository;
        this.internalOrderItemQueryService = internalOrderItemQueryService;
    }

    /**
     * {@code POST  /internal-order-items} : Create a new internalOrderItem.
     *
     * @param internalOrderItemDTO the internalOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new internalOrderItemDTO, or with status {@code 400 (Bad Request)} if the internalOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InternalOrderItemDTO> createInternalOrderItem(@Valid @RequestBody InternalOrderItemDTO internalOrderItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save InternalOrderItem : {}", internalOrderItemDTO);
        if (internalOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new internalOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        internalOrderItemDTO = internalOrderItemService.save(internalOrderItemDTO);
        return ResponseEntity.created(new URI("/api/internal-order-items/" + internalOrderItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, internalOrderItemDTO.getId().toString()))
            .body(internalOrderItemDTO);
    }

    /**
     * {@code PUT  /internal-order-items/:id} : Updates an existing internalOrderItem.
     *
     * @param id the id of the internalOrderItemDTO to save.
     * @param internalOrderItemDTO the internalOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the internalOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the internalOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InternalOrderItemDTO> updateInternalOrderItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody InternalOrderItemDTO internalOrderItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update InternalOrderItem : {}, {}", id, internalOrderItemDTO);
        if (internalOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        internalOrderItemDTO = internalOrderItemService.update(internalOrderItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, internalOrderItemDTO.getId().toString()))
            .body(internalOrderItemDTO);
    }

    /**
     * {@code PATCH  /internal-order-items/:id} : Partial updates given fields of an existing internalOrderItem, field will ignore if it is null
     *
     * @param id the id of the internalOrderItemDTO to save.
     * @param internalOrderItemDTO the internalOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the internalOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the internalOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the internalOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InternalOrderItemDTO> partialUpdateInternalOrderItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody InternalOrderItemDTO internalOrderItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update InternalOrderItem partially : {}, {}", id, internalOrderItemDTO);
        if (internalOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InternalOrderItemDTO> result = internalOrderItemService.partialUpdate(internalOrderItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, internalOrderItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /internal-order-items} : get all the internalOrderItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of internalOrderItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InternalOrderItemDTO>> getAllInternalOrderItems(InternalOrderItemCriteria criteria) {
        log.debug("REST request to get InternalOrderItems by criteria: {}", criteria);

        List<InternalOrderItemDTO> entityList = internalOrderItemQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /internal-order-items/count} : count all the internalOrderItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInternalOrderItems(InternalOrderItemCriteria criteria) {
        log.debug("REST request to count InternalOrderItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(internalOrderItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /internal-order-items/:id} : get the "id" internalOrderItem.
     *
     * @param id the id of the internalOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the internalOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InternalOrderItemDTO> getInternalOrderItem(@PathVariable("id") UUID id) {
        log.debug("REST request to get InternalOrderItem : {}", id);
        Optional<InternalOrderItemDTO> internalOrderItemDTO = internalOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(internalOrderItemDTO);
    }

    /**
     * {@code DELETE  /internal-order-items/:id} : delete the "id" internalOrderItem.
     *
     * @param id the id of the internalOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInternalOrderItem(@PathVariable("id") UUID id) {
        log.debug("REST request to delete InternalOrderItem : {}", id);
        internalOrderItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
