package com.foursquare.server.web.rest;

import com.foursquare.server.repository.ParticipantRepository;
import com.foursquare.server.service.ParticipantService;
import com.foursquare.server.service.dto.ParticipantDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.Participant}.
 */
@RestController
@RequestMapping("/api/participants")
public class ParticipantResource {

    private static final Logger log = LoggerFactory.getLogger(ParticipantResource.class);

    private static final String ENTITY_NAME = "participant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipantService participantService;

    private final ParticipantRepository participantRepository;

    public ParticipantResource(ParticipantService participantService, ParticipantRepository participantRepository) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;
    }

    /**
     * {@code POST  /participants} : Create a new participant.
     *
     * @param participantDTO the participantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantDTO, or with status {@code 400 (Bad Request)} if the participant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParticipantDTO> createParticipant(@Valid @RequestBody ParticipantDTO participantDTO) throws URISyntaxException {
        log.debug("REST request to save Participant : {}", participantDTO);
        if (participantDTO.getId() != null) {
            throw new BadRequestAlertException("A new participant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantDTO = participantService.save(participantDTO);
        return ResponseEntity.created(new URI("/api/participants/" + participantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, participantDTO.getId().toString()))
            .body(participantDTO);
    }

    /**
     * {@code PUT  /participants/:id} : Updates an existing participant.
     *
     * @param id the id of the participantDTO to save.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantDTO,
     * or with status {@code 400 (Bad Request)} if the participantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantDTO> updateParticipant(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Participant : {}, {}", id, participantDTO);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        participantDTO = participantService.update(participantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, participantDTO.getId().toString()))
            .body(participantDTO);
    }

    /**
     * {@code PATCH  /participants/:id} : Partial updates given fields of an existing participant, field will ignore if it is null
     *
     * @param id the id of the participantDTO to save.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantDTO,
     * or with status {@code 400 (Bad Request)} if the participantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the participantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the participantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantDTO> partialUpdateParticipant(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Participant partially : {}, {}", id, participantDTO);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantDTO> result = participantService.partialUpdate(participantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, participantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participants} : get all the participants.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participants in body.
     */
    @GetMapping("")
    public List<ParticipantDTO> getAllParticipants(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all Participants");
        return participantService.findAll();
    }

    /**
     * {@code GET  /participants/:id} : get the "id" participant.
     *
     * @param id the id of the participantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDTO> getParticipant(@PathVariable("id") UUID id) {
        log.debug("REST request to get Participant : {}", id);
        Optional<ParticipantDTO> participantDTO = participantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantDTO);
    }

    /**
     * {@code DELETE  /participants/:id} : delete the "id" participant.
     *
     * @param id the id of the participantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable("id") UUID id) {
        log.debug("REST request to delete Participant : {}", id);
        participantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /participants/_search?query=:query} : search for the participant corresponding
     * to the query.
     *
     * @param query the query of the participant search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ParticipantDTO> searchParticipants(@RequestParam("query") String query) {
        log.debug("REST request to search Participants for query {}", query);
        try {
            return participantService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
