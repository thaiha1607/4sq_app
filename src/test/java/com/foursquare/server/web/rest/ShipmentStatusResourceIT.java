package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ShipmentStatusAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.repository.ShipmentStatusRepository;
import com.foursquare.server.repository.search.ShipmentStatusSearchRepository;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import com.foursquare.server.service.mapper.ShipmentStatusMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
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

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shipment-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{statusCode}";
    private static final String ENTITY_SEARCH_API_URL = "/api/shipment-statuses/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentStatusRepository shipmentStatusRepository;

    @Autowired
    private ShipmentStatusMapper shipmentStatusMapper;

    @Autowired
    private ShipmentStatusSearchRepository shipmentStatusSearchRepository;

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
        ShipmentStatus shipmentStatus = new ShipmentStatus().description(DEFAULT_DESCRIPTION);
        return shipmentStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentStatus createUpdatedEntity(EntityManager em) {
        ShipmentStatus shipmentStatus = new ShipmentStatus().description(UPDATED_DESCRIPTION);
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
            shipmentStatusSearchRepository.delete(insertedShipmentStatus);
            insertedShipmentStatus = null;
        }
    }

    @Test
    @Transactional
    void createShipmentStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
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

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedShipmentStatus = returnedShipmentStatus;
    }

    @Test
    @Transactional
    void createShipmentStatusWithExistingId() throws Exception {
        // Create the ShipmentStatus with an existing ID
        shipmentStatus.setStatusCode(1L);
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        // set the field null
        shipmentStatus.setDescription(null);

        // Create the ShipmentStatus, which fails.
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        restShipmentStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllShipmentStatuses() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get all the shipmentStatusList
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=statusCode,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(shipmentStatus.getStatusCode().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getShipmentStatus() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        // Get the shipmentStatus
        restShipmentStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, shipmentStatus.getStatusCode()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.statusCode").value(shipmentStatus.getStatusCode().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
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
        shipmentStatusSearchRepository.save(shipmentStatus);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());

        // Update the shipmentStatus
        ShipmentStatus updatedShipmentStatus = shipmentStatusRepository.findById(shipmentStatus.getStatusCode()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentStatus are not directly saved in db
        em.detach(updatedShipmentStatus);
        updatedShipmentStatus.description(UPDATED_DESCRIPTION);
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(updatedShipmentStatus);

        restShipmentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentStatusDTO.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentStatusToMatchAllProperties(updatedShipmentStatus);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ShipmentStatus> shipmentStatusSearchList = Streamable.of(shipmentStatusSearchRepository.findAll()).toList();
                ShipmentStatus testShipmentStatusSearch = shipmentStatusSearchList.get(searchDatabaseSizeAfter - 1);

                assertShipmentStatusAllPropertiesEquals(testShipmentStatusSearch, updatedShipmentStatus);
            });
    }

    @Test
    @Transactional
    void putNonExistingShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        shipmentStatus.setStatusCode(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentStatusDTO.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        shipmentStatus.setStatusCode(longCount.incrementAndGet());

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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        shipmentStatus.setStatusCode(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateShipmentStatusWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentStatus using partial update
        ShipmentStatus partialUpdatedShipmentStatus = new ShipmentStatus();
        partialUpdatedShipmentStatus.setStatusCode(shipmentStatus.getStatusCode());

        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentStatus.getStatusCode())
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
        partialUpdatedShipmentStatus.setStatusCode(shipmentStatus.getStatusCode());

        partialUpdatedShipmentStatus.description(UPDATED_DESCRIPTION);

        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentStatus.getStatusCode())
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
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        shipmentStatus.setStatusCode(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentStatusDTO.getStatusCode())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        shipmentStatus.setStatusCode(longCount.incrementAndGet());

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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        shipmentStatus.setStatusCode(longCount.incrementAndGet());

        // Create the ShipmentStatus
        ShipmentStatusDTO shipmentStatusDTO = shipmentStatusMapper.toDto(shipmentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteShipmentStatus() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);
        shipmentStatusRepository.save(shipmentStatus);
        shipmentStatusSearchRepository.save(shipmentStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the shipmentStatus
        restShipmentStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentStatus.getStatusCode()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchShipmentStatus() throws Exception {
        // Initialize the database
        insertedShipmentStatus = shipmentStatusRepository.saveAndFlush(shipmentStatus);
        shipmentStatusSearchRepository.save(shipmentStatus);

        // Search the shipmentStatus
        restShipmentStatusMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + shipmentStatus.getStatusCode()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(shipmentStatus.getStatusCode().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
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
        return shipmentStatusRepository.findById(shipmentStatus.getStatusCode()).orElseThrow();
    }

    protected void assertPersistedShipmentStatusToMatchAllProperties(ShipmentStatus expectedShipmentStatus) {
        assertShipmentStatusAllPropertiesEquals(expectedShipmentStatus, getPersistedShipmentStatus(expectedShipmentStatus));
    }

    protected void assertPersistedShipmentStatusToMatchUpdatableProperties(ShipmentStatus expectedShipmentStatus) {
        assertShipmentStatusAllUpdatablePropertiesEquals(expectedShipmentStatus, getPersistedShipmentStatus(expectedShipmentStatus));
    }
}
