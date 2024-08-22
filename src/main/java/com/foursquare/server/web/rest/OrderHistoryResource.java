package com.foursquare.server.web.rest;

import com.foursquare.server.repository.OrderHistoryRepository;
import com.foursquare.server.service.OrderHistoryQueryService;
import com.foursquare.server.service.OrderHistoryService;
import com.foursquare.server.service.criteria.OrderHistoryCriteria;
import com.foursquare.server.service.dto.OrderHistoryDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.OrderHistory}.
 */
@RestController
@RequestMapping("/api/order-histories")
public class OrderHistoryResource {

    private static final Logger log = LoggerFactory.getLogger(OrderHistoryResource.class);

    private static final String ENTITY_NAME = "orderHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderHistoryService orderHistoryService;

    private final OrderHistoryRepository orderHistoryRepository;

    private final OrderHistoryQueryService orderHistoryQueryService;

    public OrderHistoryResource(
        OrderHistoryService orderHistoryService,
        OrderHistoryRepository orderHistoryRepository,
        OrderHistoryQueryService orderHistoryQueryService
    ) {
        this.orderHistoryService = orderHistoryService;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderHistoryQueryService = orderHistoryQueryService;
    }

    /**
     * {@code POST  /order-histories} : Create a new orderHistory.
     *
     * @param orderHistoryDTO the orderHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderHistoryDTO, or with status {@code 400 (Bad Request)} if the orderHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderHistoryDTO> createOrderHistory(@Valid @RequestBody OrderHistoryDTO orderHistoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save OrderHistory : {}", orderHistoryDTO);
        if (orderHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderHistoryDTO = orderHistoryService.save(orderHistoryDTO);
        return ResponseEntity.created(new URI("/api/order-histories/" + orderHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, orderHistoryDTO.getId().toString()))
            .body(orderHistoryDTO);
    }

    /**
     * {@code PUT  /order-histories/:id} : Updates an existing orderHistory.
     *
     * @param id the id of the orderHistoryDTO to save.
     * @param orderHistoryDTO the orderHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the orderHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderHistoryDTO> updateOrderHistory(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody OrderHistoryDTO orderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderHistory : {}, {}", id, orderHistoryDTO);
        if (orderHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderHistoryDTO = orderHistoryService.update(orderHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderHistoryDTO.getId().toString()))
            .body(orderHistoryDTO);
    }

    /**
     * {@code PATCH  /order-histories/:id} : Partial updates given fields of an existing orderHistory, field will ignore if it is null
     *
     * @param id the id of the orderHistoryDTO to save.
     * @param orderHistoryDTO the orderHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the orderHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderHistoryDTO> partialUpdateOrderHistory(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody OrderHistoryDTO orderHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderHistory partially : {}, {}", id, orderHistoryDTO);
        if (orderHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderHistoryDTO> result = orderHistoryService.partialUpdate(orderHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-histories} : get all the orderHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrderHistoryDTO>> getAllOrderHistories(OrderHistoryCriteria criteria) {
        log.debug("REST request to get OrderHistories by criteria: {}", criteria);

        List<OrderHistoryDTO> entityList = orderHistoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /order-histories/count} : count all the orderHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOrderHistories(OrderHistoryCriteria criteria) {
        log.debug("REST request to count OrderHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(orderHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /order-histories/:id} : get the "id" orderHistory.
     *
     * @param id the id of the orderHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderHistoryDTO> getOrderHistory(@PathVariable("id") UUID id) {
        log.debug("REST request to get OrderHistory : {}", id);
        Optional<OrderHistoryDTO> orderHistoryDTO = orderHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderHistoryDTO);
    }

    /**
     * {@code DELETE  /order-histories/:id} : delete the "id" orderHistory.
     *
     * @param id the id of the orderHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderHistory(@PathVariable("id") UUID id) {
        log.debug("REST request to delete OrderHistory : {}", id);
        orderHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
