package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ShipmentStatusAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.repository.ShipmentStatusRepository;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import com.foursquare.server.service.mapper.ShipmentStatusMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShipmentStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShipmentStatusResourceIT {

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shipment-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentStatusRepository shipmentStatusRepository;

    @Autowired
    private ShipmentStatusMapper shipmentStatusMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentStatusMockMvc;

    private ShipmentStatus shipmentStatus;

    private ShipmentStatus insertedShipmentStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentStatus createEntity(EntityManager em) {
        ShipmentStatus shipmentStatus = new ShipmentStatus().statusCode(DEFAULT_STATUS_CODE).description(DEFAULT_DESCRIPTION);
        return shipmentStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentStatus createUpdatedEntity(EntityManager em) {
        ShipmentStatus shipmentStatus = new ShipmentStatus().statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);
        return shipmentStatus;
    }

    @BeforeEach
    public void initTest() {
        shipmentStatus = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedShipmentStatus != null) {
            shipmentStatusRepository.delete(insertedShipmentStatus);
            insertedShipmentStatus = null;
        }
    }

    @Test
    @Transactional
    void createShipmentStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);
        var returnedShipmentStatusDTO = om.readValue(
            restShipmentStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentStatusDTO.class
        );

        // Validate the ShipmentStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipmentStatus = shipmentStatusMapper.toEntity(returnedShipmentStatusDTO);
        assertShipmentStatusUpdatableFieldsEquals(returnedShipmentStatus, getPersistedShipmentStatus(returnedShipmentStatus));

        insertedShipmentStatus = returnedShipmentStatus;
    }

    @Test
    @Transactional
    void createShipmentStatusWithExistingId() throws Exception {
        // Create the ShipmentStatus with an existing ID
        shipmentStatus.setId(1L);
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipmentStatus.setStatusCode(null);

        // Create the ShipmentStatus, which fails.
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        restShipmentStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShipmentStatuses() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getShipmentStatus() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get the shipmentStatus
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, shipmentStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipmentStatus.getId().intValue()))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getShipmentStatusesByIdFiltering() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        Long id = shipmentStatus.getId();

        defaultShipmentStatusFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShipmentStatusFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShipmentStatusFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByStatusCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where statusCode equals to
        defaultShipmentStatusFiltering("statusCode.equals=" + DEFAULT_STATUS_CODE, "statusCode.equals=" + UPDATED_STATUS_CODE);
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByStatusCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where statusCode in
        defaultShipmentStatusFiltering(
            "statusCode.in=" + DEFAULT_STATUS_CODE + "," + UPDATED_STATUS_CODE,
            "statusCode.in=" + UPDATED_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByStatusCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where statusCode is not null
        defaultShipmentStatusFiltering("statusCode.specified=true", "statusCode.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByStatusCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where statusCode contains
        defaultShipmentStatusFiltering("statusCode.contains=" + DEFAULT_STATUS_CODE, "statusCode.contains=" + UPDATED_STATUS_CODE);
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByStatusCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where statusCode does not contain
        defaultShipmentStatusFiltering(
            "statusCode.doesNotContain=" + UPDATED_STATUS_CODE,
            "statusCode.doesNotContain=" + DEFAULT_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where description equals to
        defaultShipmentStatusFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where description in
        defaultShipmentStatusFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where description is not null
        defaultShipmentStatusFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where description contains
        defaultShipmentStatusFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllShipmentStatusesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList where description does not contain
        defaultShipmentStatusFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    private void defaultShipmentStatusFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShipmentStatusShouldBeFound(shouldBeFound);
        defaultShipmentStatusShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentStatusShouldBeFound(String filter) throws Exception {
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentStatusShouldNotBeFound(String filter) throws Exception {
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShipmentStatus() throws Exception {
        // Get the shipmentStatus
        restShipmentStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipmentStatus() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentStatus
        ShipmentStatus updatedShipmentStatus = shipmentStatusRepository.findById(shipmentStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentStatus are not directly saved in db
        em.detach(updatedShipmentStatus);
        updatedShipmentStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(updatedShipmentStatus);

        restShipmentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentStatusToMatchAllProperties(updatedShipmentStatus);
    }

    @Test
    @Transactional
    void putNonExistingShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentStatus.setId(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentStatus.setId(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentStatus.setId(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShipmentStatusWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentStatus using partial update
        ShipmentStatus partialUpdatedShipmentStatus = new ShipmentStatus();
        partialUpdatedShipmentStatus.setId(shipmentStatus.getId());

        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentStatus))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShipmentStatus, shipmentStatus),
            getPersistedShipmentStatus(shipmentStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateShipmentStatusWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentStatus using partial update
        ShipmentStatus partialUpdatedShipmentStatus = new ShipmentStatus();
        partialUpdatedShipmentStatus.setId(shipmentStatus.getId());

        partialUpdatedShipmentStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);

        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentStatus))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentStatusUpdatableFieldsEquals(partialUpdatedShipmentStatus, getPersistedShipmentStatus(partialUpdatedShipmentStatus));
    }

    @Test
    @Transactional
    void patchNonExistingShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentStatus.setId(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentStatusDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentStatus.setId(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentStatus.setId(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipmentStatus() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipmentStatus
        restShipmentStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentStatus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentStatusRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ShipmentStatus getPersistedShipmentStatus(ShipmentStatus shipmentStatus) {
        return shipmentStatusRepository.findById(shipmentStatus.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentStatusToMatchAllProperties(ShipmentStatus expectedShipmentStatus) {
        assertShipmentStatusAllPropertiesEquals(expectedShipmentStatus, getPersistedShipmentStatus(expectedShipmentStatus));
    }

    protected void assertPersistedShipmentStatusToMatchUpdatableProperties(ShipmentStatus expectedShipmentStatus) {
        assertShipmentStatusAllUpdatablePropertiesEquals(expectedShipmentStatus, getPersistedShipmentStatus(expectedShipmentStatus));
    }
}
