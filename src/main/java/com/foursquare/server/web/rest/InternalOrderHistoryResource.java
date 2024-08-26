package com.foursquare.server.web.rest;

import com.foursquare.server.repository.InternalOrderHistoryRepository;
import com.foursquare.server.service.InternalOrderHistoryQueryService;
import com.foursquare.server.service.InternalOrderHistoryService;
import com.foursquare.server.service.criteria.InternalOrderHistoryCriteria;
import com.foursquare.server.service.dto.InternalOrderHistoryDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.InternalOrderHistory}.
 */
@RestController
@RequestMapping("/api/internal-order-histories")
public class InternalOrderHistoryResource {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderHistoryResource.class);

    private static final String ENTITY_NAME = "internalOrderHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalOrderHistoryService internalOrderHistoryService;

    private final InternalOrderHistoryRepository internalOrderHistoryRepository;

    private final InternalOrderHistoryQueryService internalOrderHistoryQueryService;

    public InternalOrderHistoryResource(
        InternalOrderHistoryService internalOrderHistoryService,
        InternalOrderHistoryRepository internalOrderHistoryRepository,
        InternalOrderHistoryQueryService internalOrderHistoryQueryService
    ) {
        this.internalOrderHistoryService = internalOrderHistoryService;
        this.internalOrderHistoryRepository = internalOrderHistoryRepository;
        this.internalOrderHistoryQueryService = internalOrderHistoryQueryService;
    }

    /**
     * {@code POST  /internal-order-histories} : Create a new internalOrderHistory.
     *
     * @param internalOrderHistoryDTO the internalOrderHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new internalOrderHistoryDTO, or with status {@code 400 (Bad Request)} if the internalOrderHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InternalOrderHistoryDTO> createInternalOrderHistory(
        @Valid @RequestBody InternalOrderHistoryDTO internalOrderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to save InternalOrderHistory : {}", internalOrderHistoryDTO);
        if (internalOrderHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new internalOrderHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        internalOrderHistoryDTO = internalOrderHistoryService.save(internalOrderHistoryDTO);
        return ResponseEntity.created(new URI("/api/internal-order-histories/" + internalOrderHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, internalOrderHistoryDTO.getId().toString()))
            .body(internalOrderHistoryDTO);
    }

    /**
     * {@code PUT  /internal-order-histories/:id} : Updates an existing internalOrderHistory.
     *
     * @param id the id of the internalOrderHistoryDTO to save.
     * @param internalOrderHistoryDTO the internalOrderHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalOrderHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the internalOrderHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the internalOrderHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InternalOrderHistoryDTO> updateInternalOrderHistory(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody InternalOrderHistoryDTO internalOrderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update InternalOrderHistory : {}, {}", id, internalOrderHistoryDTO);
        if (internalOrderHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalOrderHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalOrderHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        internalOrderHistoryDTO = internalOrderHistoryService.update(internalOrderHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, internalOrderHistoryDTO.getId().toString()))
            .body(internalOrderHistoryDTO);
    }

    /**
     * {@code PATCH  /internal-order-histories/:id} : Partial updates given fields of an existing internalOrderHistory, field will ignore if it is null
     *
     * @param id the id of the internalOrderHistoryDTO to save.
     * @param internalOrderHistoryDTO the internalOrderHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalOrderHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the internalOrderHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the internalOrderHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the internalOrderHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InternalOrderHistoryDTO> partialUpdateInternalOrderHistory(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody InternalOrderHistoryDTO internalOrderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update InternalOrderHistory partially : {}, {}", id, internalOrderHistoryDTO);
        if (internalOrderHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalOrderHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalOrderHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InternalOrderHistoryDTO> result = internalOrderHistoryService.partialUpdate(internalOrderHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, internalOrderHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /internal-order-histories} : get all the internalOrderHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of internalOrderHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InternalOrderHistoryDTO>> getAllInternalOrderHistories(InternalOrderHistoryCriteria criteria) {
        log.debug("REST request to get InternalOrderHistories by criteria: {}", criteria);

        List<InternalOrderHistoryDTO> entityList = internalOrderHistoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /internal-order-histories/count} : count all the internalOrderHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInternalOrderHistories(InternalOrderHistoryCriteria criteria) {
        log.debug("REST request to count InternalOrderHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(internalOrderHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /internal-order-histories/:id} : get the "id" internalOrderHistory.
     *
     * @param id the id of the internalOrderHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the internalOrderHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InternalOrderHistoryDTO> getInternalOrderHistory(@PathVariable("id") UUID id) {
        log.debug("REST request to get InternalOrderHistory : {}", id);
        Optional<InternalOrderHistoryDTO> internalOrderHistoryDTO = internalOrderHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(internalOrderHistoryDTO);
    }

    /**
     * {@code DELETE  /internal-order-histories/:id} : delete the "id" internalOrderHistory.
     *
     * @param id the id of the internalOrderHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInternalOrderHistory(@PathVariable("id") UUID id) {
        log.debug("REST request to delete InternalOrderHistory : {}", id);
        internalOrderHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
