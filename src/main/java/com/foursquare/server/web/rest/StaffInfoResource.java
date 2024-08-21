package com.foursquare.server.web.rest;

import com.foursquare.server.repository.StaffInfoRepository;
import com.foursquare.server.service.StaffInfoService;
import com.foursquare.server.service.dto.StaffInfoDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.StaffInfo}.
 */
@RestController
@RequestMapping("/api/staff-infos")
public class StaffInfoResource {

    private static final Logger log = LoggerFactory.getLogger(StaffInfoResource.class);

    private static final String ENTITY_NAME = "staffInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StaffInfoService staffInfoService;

    private final StaffInfoRepository staffInfoRepository;

    public StaffInfoResource(StaffInfoService staffInfoService, StaffInfoRepository staffInfoRepository) {
        this.staffInfoService = staffInfoService;
        this.staffInfoRepository = staffInfoRepository;
    }

    /**
     * {@code POST  /staff-infos} : Create a new staffInfo.
     *
     * @param staffInfoDTO the staffInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new staffInfoDTO, or with status {@code 400 (Bad Request)} if the staffInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StaffInfoDTO> createStaffInfo(@Valid @RequestBody StaffInfoDTO staffInfoDTO) throws URISyntaxException {
        log.debug("REST request to save StaffInfo : {}", staffInfoDTO);
        if (staffInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new staffInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        staffInfoDTO = staffInfoService.save(staffInfoDTO);
        return ResponseEntity.created(new URI("/api/staff-infos/" + staffInfoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, staffInfoDTO.getId().toString()))
            .body(staffInfoDTO);
    }

    /**
     * {@code PUT  /staff-infos/:id} : Updates an existing staffInfo.
     *
     * @param id the id of the staffInfoDTO to save.
     * @param staffInfoDTO the staffInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffInfoDTO,
     * or with status {@code 400 (Bad Request)} if the staffInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the staffInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StaffInfoDTO> updateStaffInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StaffInfoDTO staffInfoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StaffInfo : {}, {}", id, staffInfoDTO);
        if (staffInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staffInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        staffInfoDTO = staffInfoService.update(staffInfoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, staffInfoDTO.getId().toString()))
            .body(staffInfoDTO);
    }

    /**
     * {@code PATCH  /staff-infos/:id} : Partial updates given fields of an existing staffInfo, field will ignore if it is null
     *
     * @param id the id of the staffInfoDTO to save.
     * @param staffInfoDTO the staffInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffInfoDTO,
     * or with status {@code 400 (Bad Request)} if the staffInfoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the staffInfoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the staffInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StaffInfoDTO> partialUpdateStaffInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StaffInfoDTO staffInfoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StaffInfo partially : {}, {}", id, staffInfoDTO);
        if (staffInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staffInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StaffInfoDTO> result = staffInfoService.partialUpdate(staffInfoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, staffInfoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /staff-infos} : get all the staffInfos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of staffInfos in body.
     */
    @GetMapping("")
    public List<StaffInfoDTO> getAllStaffInfos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all StaffInfos");
        return staffInfoService.findAll();
    }

    /**
     * {@code GET  /staff-infos/:id} : get the "id" staffInfo.
     *
     * @param id the id of the staffInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the staffInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StaffInfoDTO> getStaffInfo(@PathVariable("id") Long id) {
        log.debug("REST request to get StaffInfo : {}", id);
        Optional<StaffInfoDTO> staffInfoDTO = staffInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(staffInfoDTO);
    }

    /**
     * {@code DELETE  /staff-infos/:id} : delete the "id" staffInfo.
     *
     * @param id the id of the staffInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffInfo(@PathVariable("id") Long id) {
        log.debug("REST request to delete StaffInfo : {}", id);
        staffInfoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
