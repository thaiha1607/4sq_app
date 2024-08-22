package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ProductImageRepository;
import com.foursquare.server.service.ProductImageQueryService;
import com.foursquare.server.service.ProductImageService;
import com.foursquare.server.service.criteria.ProductImageCriteria;
import com.foursquare.server.service.dto.ProductImageDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.ProductImage}.
 */
@RestController
@RequestMapping("/api/product-images")
public class ProductImageResource {

    private static final Logger log = LoggerFactory.getLogger(ProductImageResource.class);

    private static final String ENTITY_NAME = "productImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductImageService productImageService;

    private final ProductImageRepository productImageRepository;

    private final ProductImageQueryService productImageQueryService;

    public ProductImageResource(
        ProductImageService productImageService,
        ProductImageRepository productImageRepository,
        ProductImageQueryService productImageQueryService
    ) {
        this.productImageService = productImageService;
        this.productImageRepository = productImageRepository;
        this.productImageQueryService = productImageQueryService;
    }

    /**
     * {@code POST  /product-images} : Create a new productImage.
     *
     * @param productImageDTO the productImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productImageDTO, or with status {@code 400 (Bad Request)} if the productImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductImageDTO> createProductImage(@Valid @RequestBody ProductImageDTO productImageDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductImage : {}", productImageDTO);
        if (productImageDTO.getId() != null) {
            throw new BadRequestAlertException("A new productImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productImageDTO = productImageService.save(productImageDTO);
        return ResponseEntity.created(new URI("/api/product-images/" + productImageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, productImageDTO.getId().toString()))
            .body(productImageDTO);
    }

    /**
     * {@code PUT  /product-images/:id} : Updates an existing productImage.
     *
     * @param id the id of the productImageDTO to save.
     * @param productImageDTO the productImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productImageDTO,
     * or with status {@code 400 (Bad Request)} if the productImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductImageDTO> updateProductImage(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ProductImageDTO productImageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductImage : {}, {}", id, productImageDTO);
        if (productImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productImageDTO = productImageService.update(productImageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productImageDTO.getId().toString()))
            .body(productImageDTO);
    }

    /**
     * {@code PATCH  /product-images/:id} : Partial updates given fields of an existing productImage, field will ignore if it is null
     *
     * @param id the id of the productImageDTO to save.
     * @param productImageDTO the productImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productImageDTO,
     * or with status {@code 400 (Bad Request)} if the productImageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productImageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductImageDTO> partialUpdateProductImage(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ProductImageDTO productImageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductImage partially : {}, {}", id, productImageDTO);
        if (productImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductImageDTO> result = productImageService.partialUpdate(productImageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productImageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-images} : get all the productImages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productImages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductImageDTO>> getAllProductImages(ProductImageCriteria criteria) {
        log.debug("REST request to get ProductImages by criteria: {}", criteria);

        List<ProductImageDTO> entityList = productImageQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /product-images/count} : count all the productImages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProductImages(ProductImageCriteria criteria) {
        log.debug("REST request to count ProductImages by criteria: {}", criteria);
        return ResponseEntity.ok().body(productImageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-images/:id} : get the "id" productImage.
     *
     * @param id the id of the productImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductImageDTO> getProductImage(@PathVariable("id") UUID id) {
        log.debug("REST request to get ProductImage : {}", id);
        Optional<ProductImageDTO> productImageDTO = productImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productImageDTO);
    }

    /**
     * {@code DELETE  /product-images/:id} : delete the "id" productImage.
     *
     * @param id the id of the productImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable("id") UUID id) {
        log.debug("REST request to delete ProductImage : {}", id);
        productImageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
