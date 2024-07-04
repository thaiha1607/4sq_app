package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ProductQuantityRepository;
import com.foursquare.server.service.ProductQuantityService;
import com.foursquare.server.service.dto.ProductQuantityDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.ProductQuantity}.
 */
@RestController
@RequestMapping("/api/product-quantities")
public class ProductQuantityResource {

    private static final Logger log = LoggerFactory.getLogger(ProductQuantityResource.class);

    private static final String ENTITY_NAME = "productQuantity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductQuantityService productQuantityService;

    private final ProductQuantityRepository productQuantityRepository;

    public ProductQuantityResource(ProductQuantityService productQuantityService, ProductQuantityRepository productQuantityRepository) {
        this.productQuantityService = productQuantityService;
        this.productQuantityRepository = productQuantityRepository;
    }

    /**
     * {@code POST  /product-quantities} : Create a new productQuantity.
     *
     * @param productQuantityDTO the productQuantityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productQuantityDTO, or with status {@code 400 (Bad Request)} if the productQuantity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductQuantityDTO> createProductQuantity(@Valid @RequestBody ProductQuantityDTO productQuantityDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductQuantity : {}", productQuantityDTO);
        if (productQuantityDTO.getId() != null) {
            throw new BadRequestAlertException("A new productQuantity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productQuantityDTO = productQuantityService.save(productQuantityDTO);
        return ResponseEntity.created(new URI("/api/product-quantities/" + productQuantityDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, productQuantityDTO.getId().toString()))
            .body(productQuantityDTO);
    }

    /**
     * {@code PUT  /product-quantities/:id} : Updates an existing productQuantity.
     *
     * @param id the id of the productQuantityDTO to save.
     * @param productQuantityDTO the productQuantityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productQuantityDTO,
     * or with status {@code 400 (Bad Request)} if the productQuantityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productQuantityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductQuantityDTO> updateProductQuantity(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ProductQuantityDTO productQuantityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductQuantity : {}, {}", id, productQuantityDTO);
        if (productQuantityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productQuantityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productQuantityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productQuantityDTO = productQuantityService.update(productQuantityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productQuantityDTO.getId().toString()))
            .body(productQuantityDTO);
    }

    /**
     * {@code PATCH  /product-quantities/:id} : Partial updates given fields of an existing productQuantity, field will ignore if it is null
     *
     * @param id the id of the productQuantityDTO to save.
     * @param productQuantityDTO the productQuantityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productQuantityDTO,
     * or with status {@code 400 (Bad Request)} if the productQuantityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productQuantityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productQuantityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductQuantityDTO> partialUpdateProductQuantity(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ProductQuantityDTO productQuantityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductQuantity partially : {}, {}", id, productQuantityDTO);
        if (productQuantityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productQuantityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productQuantityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductQuantityDTO> result = productQuantityService.partialUpdate(productQuantityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productQuantityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-quantities} : get all the productQuantities.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productQuantities in body.
     */
    @GetMapping("")
    public List<ProductQuantityDTO> getAllProductQuantities(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all ProductQuantities");
        return productQuantityService.findAll();
    }

    /**
     * {@code GET  /product-quantities/:id} : get the "id" productQuantity.
     *
     * @param id the id of the productQuantityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productQuantityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductQuantityDTO> getProductQuantity(@PathVariable("id") UUID id) {
        log.debug("REST request to get ProductQuantity : {}", id);
        Optional<ProductQuantityDTO> productQuantityDTO = productQuantityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productQuantityDTO);
    }

    /**
     * {@code DELETE  /product-quantities/:id} : delete the "id" productQuantity.
     *
     * @param id the id of the productQuantityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductQuantity(@PathVariable("id") UUID id) {
        log.debug("REST request to delete ProductQuantity : {}", id);
        productQuantityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /product-quantities/_search?query=:query} : search for the productQuantity corresponding
     * to the query.
     *
     * @param query the query of the productQuantity search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ProductQuantityDTO> searchProductQuantities(@RequestParam("query") String query) {
        log.debug("REST request to search ProductQuantities for query {}", query);
        try {
            return productQuantityService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
