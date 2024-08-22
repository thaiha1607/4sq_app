package com.foursquare.server.web.rest;

import com.foursquare.server.repository.InvoiceStatusRepository;
import com.foursquare.server.service.InvoiceStatusQueryService;
import com.foursquare.server.service.InvoiceStatusService;
import com.foursquare.server.service.criteria.InvoiceStatusCriteria;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.InvoiceStatus}.
 */
@RestController
@RequestMapping("/api/invoice-statuses")
public class InvoiceStatusResource {

    private static final Logger log = LoggerFactory.getLogger(InvoiceStatusResource.class);

    private static final String ENTITY_NAME = "invoiceStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoiceStatusService invoiceStatusService;

    private final InvoiceStatusRepository invoiceStatusRepository;

    private final InvoiceStatusQueryService invoiceStatusQueryService;

    public InvoiceStatusResource(
        InvoiceStatusService invoiceStatusService,
        InvoiceStatusRepository invoiceStatusRepository,
        InvoiceStatusQueryService invoiceStatusQueryService
    ) {
        this.invoiceStatusService = invoiceStatusService;
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.invoiceStatusQueryService = invoiceStatusQueryService;
    }

    /**
     * {@code POST  /invoice-statuses} : Create a new invoiceStatus.
     *
     * @param invoiceStatusDTO the invoiceStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoiceStatusDTO, or with status {@code 400 (Bad Request)} if the invoiceStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InvoiceStatusDTO> createInvoiceStatus(@Valid @RequestBody InvoiceStatusDTO invoiceStatusDTO)
        throws URISyntaxException {
        log.debug("REST request to save InvoiceStatus : {}", invoiceStatusDTO);
        if (invoiceStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new invoiceStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        invoiceStatusDTO = invoiceStatusService.save(invoiceStatusDTO);
        return ResponseEntity.created(new URI("/api/invoice-statuses/" + invoiceStatusDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, invoiceStatusDTO.getId().toString()))
            .body(invoiceStatusDTO);
    }

    /**
     * {@code PUT  /invoice-statuses/:id} : Updates an existing invoiceStatus.
     *
     * @param id the id of the invoiceStatusDTO to save.
     * @param invoiceStatusDTO the invoiceStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceStatusDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoiceStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceStatusDTO> updateInvoiceStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InvoiceStatusDTO invoiceStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to update InvoiceStatus : {}, {}", id, invoiceStatusDTO);
        if (invoiceStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!invoiceStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        invoiceStatusDTO = invoiceStatusService.update(invoiceStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, invoiceStatusDTO.getId().toString()))
            .body(invoiceStatusDTO);
    }

    /**
     * {@code PATCH  /invoice-statuses/:id} : Partial updates given fields of an existing invoiceStatus, field will ignore if it is null
     *
     * @param id the id of the invoiceStatusDTO to save.
     * @param invoiceStatusDTO the invoiceStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoiceStatusDTO,
     * or with status {@code 400 (Bad Request)} if the invoiceStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the invoiceStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the invoiceStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InvoiceStatusDTO> partialUpdateInvoiceStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InvoiceStatusDTO invoiceStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update InvoiceStatus partially : {}, {}", id, invoiceStatusDTO);
        if (invoiceStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, invoiceStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!invoiceStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InvoiceStatusDTO> result = invoiceStatusService.partialUpdate(invoiceStatusDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, invoiceStatusDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /invoice-statuses} : get all the invoiceStatuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoiceStatuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InvoiceStatusDTO>> getAllInvoiceStatuses(InvoiceStatusCriteria criteria) {
        log.debug("REST request to get InvoiceStatuses by criteria: {}", criteria);

        List<InvoiceStatusDTO> entityList = invoiceStatusQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /invoice-statuses/count} : count all the invoiceStatuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInvoiceStatuses(InvoiceStatusCriteria criteria) {
        log.debug("REST request to count InvoiceStatuses by criteria: {}", criteria);
        return ResponseEntity.ok().body(invoiceStatusQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /invoice-statuses/:id} : get the "id" invoiceStatus.
     *
     * @param id the id of the invoiceStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoiceStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceStatusDTO> getInvoiceStatus(@PathVariable("id") Long id) {
        log.debug("REST request to get InvoiceStatus : {}", id);
        Optional<InvoiceStatusDTO> invoiceStatusDTO = invoiceStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(invoiceStatusDTO);
    }

    /**
     * {@code DELETE  /invoice-statuses/:id} : delete the "id" invoiceStatus.
     *
     * @param id the id of the invoiceStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoiceStatus(@PathVariable("id") Long id) {
        log.debug("REST request to delete InvoiceStatus : {}", id);
        invoiceStatusService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
