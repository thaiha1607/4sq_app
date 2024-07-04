package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ColourRepository;
import com.foursquare.server.service.ColourService;
import com.foursquare.server.service.dto.ColourDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.Colour}.
 */
@RestController
@RequestMapping("/api/colours")
public class ColourResource {

    private static final Logger log = LoggerFactory.getLogger(ColourResource.class);

    private static final String ENTITY_NAME = "colour";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ColourService colourService;

    private final ColourRepository colourRepository;

    public ColourResource(ColourService colourService, ColourRepository colourRepository) {
        this.colourService = colourService;
        this.colourRepository = colourRepository;
    }

    /**
     * {@code POST  /colours} : Create a new colour.
     *
     * @param colourDTO the colourDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new colourDTO, or with status {@code 400 (Bad Request)} if the colour has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ColourDTO> createColour(@Valid @RequestBody ColourDTO colourDTO) throws URISyntaxException {
        log.debug("REST request to save Colour : {}", colourDTO);
        if (colourDTO.getId() != null) {
            throw new BadRequestAlertException("A new colour cannot already have an ID", ENTITY_NAME, "idexists");
        }
        colourDTO = colourService.save(colourDTO);
        return ResponseEntity.created(new URI("/api/colours/" + colourDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, colourDTO.getId().toString()))
            .body(colourDTO);
    }

    /**
     * {@code PUT  /colours/:id} : Updates an existing colour.
     *
     * @param id the id of the colourDTO to save.
     * @param colourDTO the colourDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colourDTO,
     * or with status {@code 400 (Bad Request)} if the colourDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the colourDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ColourDTO> updateColour(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ColourDTO colourDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Colour : {}, {}", id, colourDTO);
        if (colourDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colourDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!colourRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        colourDTO = colourService.update(colourDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, colourDTO.getId().toString()))
            .body(colourDTO);
    }

    /**
     * {@code PATCH  /colours/:id} : Partial updates given fields of an existing colour, field will ignore if it is null
     *
     * @param id the id of the colourDTO to save.
     * @param colourDTO the colourDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colourDTO,
     * or with status {@code 400 (Bad Request)} if the colourDTO is not valid,
     * or with status {@code 404 (Not Found)} if the colourDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the colourDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ColourDTO> partialUpdateColour(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ColourDTO colourDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Colour partially : {}, {}", id, colourDTO);
        if (colourDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colourDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!colourRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ColourDTO> result = colourService.partialUpdate(colourDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, colourDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /colours} : get all the colours.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of colours in body.
     */
    @GetMapping("")
    public List<ColourDTO> getAllColours() {
        log.debug("REST request to get all Colours");
        return colourService.findAll();
    }

    /**
     * {@code GET  /colours/:id} : get the "id" colour.
     *
     * @param id the id of the colourDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the colourDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ColourDTO> getColour(@PathVariable("id") UUID id) {
        log.debug("REST request to get Colour : {}", id);
        Optional<ColourDTO> colourDTO = colourService.findOne(id);
        return ResponseUtil.wrapOrNotFound(colourDTO);
    }

    /**
     * {@code DELETE  /colours/:id} : delete the "id" colour.
     *
     * @param id the id of the colourDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColour(@PathVariable("id") UUID id) {
        log.debug("REST request to delete Colour : {}", id);
        colourService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /colours/_search?query=:query} : search for the colour corresponding
     * to the query.
     *
     * @param query the query of the colour search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ColourDTO> searchColours(@RequestParam("query") String query) {
        log.debug("REST request to search Colours for query {}", query);
        try {
            return colourService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
