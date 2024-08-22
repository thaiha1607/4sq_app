package com.foursquare.server.web.rest;

import com.foursquare.server.repository.UserAddressRepository;
import com.foursquare.server.service.UserAddressQueryService;
import com.foursquare.server.service.UserAddressService;
import com.foursquare.server.service.criteria.UserAddressCriteria;
import com.foursquare.server.service.dto.UserAddressDTO;
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
 * REST controller for managing {@link com.foursquare.server.domain.UserAddress}.
 */
@RestController
@RequestMapping("/api/user-addresses")
public class UserAddressResource {

    private static final Logger log = LoggerFactory.getLogger(UserAddressResource.class);

    private static final String ENTITY_NAME = "userAddress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAddressService userAddressService;

    private final UserAddressRepository userAddressRepository;

    private final UserAddressQueryService userAddressQueryService;

    public UserAddressResource(
        UserAddressService userAddressService,
        UserAddressRepository userAddressRepository,
        UserAddressQueryService userAddressQueryService
    ) {
        this.userAddressService = userAddressService;
        this.userAddressRepository = userAddressRepository;
        this.userAddressQueryService = userAddressQueryService;
    }

    /**
     * {@code POST  /user-addresses} : Create a new userAddress.
     *
     * @param userAddressDTO the userAddressDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAddressDTO, or with status {@code 400 (Bad Request)} if the userAddress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserAddressDTO> createUserAddress(@Valid @RequestBody UserAddressDTO userAddressDTO) throws URISyntaxException {
        log.debug("REST request to save UserAddress : {}", userAddressDTO);
        if (userAddressDTO.getId() != null) {
            throw new BadRequestAlertException("A new userAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userAddressDTO = userAddressService.save(userAddressDTO);
        return ResponseEntity.created(new URI("/api/user-addresses/" + userAddressDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userAddressDTO.getId().toString()))
            .body(userAddressDTO);
    }

    /**
     * {@code PUT  /user-addresses/:id} : Updates an existing userAddress.
     *
     * @param id the id of the userAddressDTO to save.
     * @param userAddressDTO the userAddressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAddressDTO,
     * or with status {@code 400 (Bad Request)} if the userAddressDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAddressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAddressDTO> updateUserAddress(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody UserAddressDTO userAddressDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserAddress : {}, {}", id, userAddressDTO);
        if (userAddressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAddressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAddressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userAddressDTO = userAddressService.update(userAddressDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userAddressDTO.getId().toString()))
            .body(userAddressDTO);
    }

    /**
     * {@code PATCH  /user-addresses/:id} : Partial updates given fields of an existing userAddress, field will ignore if it is null
     *
     * @param id the id of the userAddressDTO to save.
     * @param userAddressDTO the userAddressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAddressDTO,
     * or with status {@code 400 (Bad Request)} if the userAddressDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userAddressDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userAddressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserAddressDTO> partialUpdateUserAddress(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody UserAddressDTO userAddressDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserAddress partially : {}, {}", id, userAddressDTO);
        if (userAddressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAddressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAddressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserAddressDTO> result = userAddressService.partialUpdate(userAddressDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userAddressDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-addresses} : get all the userAddresses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAddresses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserAddressDTO>> getAllUserAddresses(UserAddressCriteria criteria) {
        log.debug("REST request to get UserAddresses by criteria: {}", criteria);

        List<UserAddressDTO> entityList = userAddressQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-addresses/count} : count all the userAddresses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUserAddresses(UserAddressCriteria criteria) {
        log.debug("REST request to count UserAddresses by criteria: {}", criteria);
        return ResponseEntity.ok().body(userAddressQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-addresses/:id} : get the "id" userAddress.
     *
     * @param id the id of the userAddressDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAddressDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAddressDTO> getUserAddress(@PathVariable("id") UUID id) {
        log.debug("REST request to get UserAddress : {}", id);
        Optional<UserAddressDTO> userAddressDTO = userAddressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userAddressDTO);
    }

    /**
     * {@code DELETE  /user-addresses/:id} : delete the "id" userAddress.
     *
     * @param id the id of the userAddressDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable("id") UUID id) {
        log.debug("REST request to delete UserAddress : {}", id);
        userAddressService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
