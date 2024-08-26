package com.foursquare.server.web.rest;

import com.foursquare.server.repository.InternalOrderRepository;
import com.foursquare.server.service.InternalOrderQueryService;
import com.foursquare.server.service.InternalOrderService;
import com.foursquare.server.service.criteria.InternalOrderCriteria;
import com.foursquare.server.service.dto.InternalOrderDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.InternalOrder}.
 */
@RestController
@RequestMapping("/api/internal-orders")
public class InternalOrderResource {

    private static final Logger log = LoggerFactory.getLogger(InternalOrderResource.class);

    private static final String ENTITY_NAME = "internalOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalOrderService internalOrderService;

    private final InternalOrderRepository internalOrderRepository;

    private final InternalOrderQueryService internalOrderQueryService;

    public InternalOrderResource(
        InternalOrderService internalOrderService,
        InternalOrderRepository internalOrderRepository,
        InternalOrderQueryService internalOrderQueryService
    ) {
        this.internalOrderService = internalOrderService;
        this.internalOrderRepository = internalOrderRepository;
        this.internalOrderQueryService = internalOrderQueryService;
    }

    /**
     * {@code POST  /internal-orders} : Create a new internalOrder.
     *
     * @param internalOrderDTO the internalOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new internalOrderDTO, or with status {@code 400 (Bad Request)} if the internalOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InternalOrderDTO> createInternalOrder(@Valid @RequestBody InternalOrderDTO internalOrderDTO)
        throws URISyntaxException {
        log.debug("REST request to save InternalOrder : {}", internalOrderDTO);
        if (internalOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new internalOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        internalOrderDTO = internalOrderService.save(internalOrderDTO);
        return ResponseEntity.created(new URI("/api/internal-orders/" + internalOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, internalOrderDTO.getId().toString()))
            .body(internalOrderDTO);
    }

    /**
     * {@code PUT  /internal-orders/:id} : Updates an existing internalOrder.
     *
     * @param id the id of the internalOrderDTO to save.
     * @param internalOrderDTO the internalOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalOrderDTO,
     * or with status {@code 400 (Bad Request)} if the internalOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the internalOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InternalOrderDTO> updateInternalOrder(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody InternalOrderDTO internalOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update InternalOrder : {}, {}", id, internalOrderDTO);
        if (internalOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        internalOrderDTO = internalOrderService.update(internalOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, internalOrderDTO.getId().toString()))
            .body(internalOrderDTO);
    }

    /**
     * {@code PATCH  /internal-orders/:id} : Partial updates given fields of an existing internalOrder, field will ignore if it is null
     *
     * @param id the id of the internalOrderDTO to save.
     * @param internalOrderDTO the internalOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated internalOrderDTO,
     * or with status {@code 400 (Bad Request)} if the internalOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the internalOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the internalOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InternalOrderDTO> partialUpdateInternalOrder(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody InternalOrderDTO internalOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update InternalOrder partially : {}, {}", id, internalOrderDTO);
        if (internalOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, internalOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!internalOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InternalOrderDTO> result = internalOrderService.partialUpdate(internalOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, internalOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /internal-orders} : get all the internalOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of internalOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InternalOrderDTO>> getAllInternalOrders(InternalOrderCriteria criteria) {
        log.debug("REST request to get InternalOrders by criteria: {}", criteria);

        List<InternalOrderDTO> entityList = internalOrderQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /internal-orders/count} : count all the internalOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInternalOrders(InternalOrderCriteria criteria) {
        log.debug("REST request to count InternalOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(internalOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /internal-orders/:id} : get the "id" internalOrder.
     *
     * @param id the id of the internalOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the internalOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InternalOrderDTO> getInternalOrder(@PathVariable("id") UUID id) {
        log.debug("REST request to get InternalOrder : {}", id);
        Optional<InternalOrderDTO> internalOrderDTO = internalOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(internalOrderDTO);
    }

    /**
     * {@code DELETE  /internal-orders/:id} : delete the "id" internalOrder.
     *
     * @param id the id of the internalOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInternalOrder(@PathVariable("id") UUID id) {
        log.debug("REST request to delete InternalOrder : {}", id);
        internalOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
