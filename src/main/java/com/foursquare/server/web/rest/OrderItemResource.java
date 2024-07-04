package com.foursquare.server.web.rest;

import com.foursquare.server.repository.OrderItemRepository;
import com.foursquare.server.service.OrderItemService;
import com.foursquare.server.service.dto.OrderItemDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.OrderItem}.
 */
@RestController
@RequestMapping("/api/order-items")
public class OrderItemResource {

    private static final Logger log = LoggerFactory.getLogger(OrderItemResource.class);

    private static final String ENTITY_NAME = "orderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemService orderItemService;

    private final OrderItemRepository orderItemRepository;

    public OrderItemResource(OrderItemService orderItemService, OrderItemRepository orderItemRepository) {
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * {@code POST  /order-items} : Create a new orderItem.
     *
     * @param orderItemDTO the orderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItemDTO, or with status {@code 400 (Bad Request)} if the orderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderItemDTO> createOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) throws URISyntaxException {
        log.debug("REST request to save OrderItem : {}", orderItemDTO);
        if (orderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderItemDTO = orderItemService.save(orderItemDTO);
        return ResponseEntity.created(new URI("/api/order-items/" + orderItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, orderItemDTO.getId().toString()))
            .body(orderItemDTO);
    }

    /**
     * {@code PUT  /order-items/:id} : Updates an existing orderItem.
     *
     * @param id the id of the orderItemDTO to save.
     * @param orderItemDTO the orderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody OrderItemDTO orderItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderItem : {}, {}", id, orderItemDTO);
        if (orderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderItemDTO = orderItemService.update(orderItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderItemDTO.getId().toString()))
            .body(orderItemDTO);
    }

    /**
     * {@code PATCH  /order-items/:id} : Partial updates given fields of an existing orderItem, field will ignore if it is null
     *
     * @param id the id of the orderItemDTO to save.
     * @param orderItemDTO the orderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderItemDTO> partialUpdateOrderItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody OrderItemDTO orderItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderItem partially : {}, {}", id, orderItemDTO);
        if (orderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderItemDTO> result = orderItemService.partialUpdate(orderItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-items} : get all the orderItems.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItems in body.
     */
    @GetMapping("")
    public List<OrderItemDTO> getAllOrderItems(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all OrderItems");
        return orderItemService.findAll();
    }

    /**
     * {@code GET  /order-items/:id} : get the "id" orderItem.
     *
     * @param id the id of the orderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItem(@PathVariable("id") UUID id) {
        log.debug("REST request to get OrderItem : {}", id);
        Optional<OrderItemDTO> orderItemDTO = orderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderItemDTO);
    }

    /**
     * {@code DELETE  /order-items/:id} : delete the "id" orderItem.
     *
     * @param id the id of the orderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable("id") UUID id) {
        log.debug("REST request to delete OrderItem : {}", id);
        orderItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /order-items/_search?query=:query} : search for the orderItem corresponding
     * to the query.
     *
     * @param query the query of the orderItem search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<OrderItemDTO> searchOrderItems(@RequestParam("query") String query) {
        log.debug("REST request to search OrderItems for query {}", query);
        try {
            return orderItemService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
