package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.InvoiceStatusAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.repository.InvoiceStatusRepository;
import com.foursquare.server.repository.search.InvoiceStatusSearchRepository;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
import com.foursquare.server.service.mapper.InvoiceStatusMapper;
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
 * Integration tests for the {@link InvoiceStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InvoiceStatusResourceIT {

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/invoice-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/invoice-statuses/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceStatusRepository invoiceStatusRepository;

    @Autowired
    private InvoiceStatusMapper invoiceStatusMapper;

    @Autowired
    private InvoiceStatusSearchRepository invoiceStatusSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceStatusMockMvc;

    private InvoiceStatus invoiceStatus;

    private InvoiceStatus insertedInvoiceStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceStatus createEntity(EntityManager em) {
        InvoiceStatus invoiceStatus = new InvoiceStatus().statusCode(DEFAULT_STATUS_CODE).description(DEFAULT_DESCRIPTION);
        return invoiceStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InvoiceStatus createUpdatedEntity(EntityManager em) {
        InvoiceStatus invoiceStatus = new InvoiceStatus().statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);
        return invoiceStatus;
    }

    @BeforeEach
    public void initTest() {
        invoiceStatus = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInvoiceStatus != null) {
            invoiceStatusRepository.delete(insertedInvoiceStatus);
            invoiceStatusSearchRepository.delete(insertedInvoiceStatus);
            insertedInvoiceStatus = null;
        }
    }

    @Test
    @Transactional
    void createInvoiceStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);
        var returnedInvoiceStatusDTO = om.readValue(
            restInvoiceStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceStatusDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InvoiceStatusDTO.class
        );

        // Validate the InvoiceStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInvoiceStatus = invoiceStatusMapper.toEntity(returnedInvoiceStatusDTO);
        assertInvoiceStatusUpdatableFieldsEquals(returnedInvoiceStatus, getPersistedInvoiceStatus(returnedInvoiceStatus));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInvoiceStatus = returnedInvoiceStatus;
    }

    @Test
    @Transactional
    void createInvoiceStatusWithExistingId() throws Exception {
        // Create the InvoiceStatus with an existing ID
        invoiceStatus.setId(1L);
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        // set the field null
        invoiceStatus.setStatusCode(null);

        // Create the InvoiceStatus, which fails.
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        restInvoiceStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceStatusDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInvoiceStatuses() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);

        // Get all the invoiceStatusList
        restInvoiceStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getInvoiceStatus() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);

        // Get the invoiceStatus
        restInvoiceStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, invoiceStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoiceStatus.getId().intValue()))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingInvoiceStatus() throws Exception {
        // Get the invoiceStatus
        restInvoiceStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoiceStatus() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoiceStatusSearchRepository.save(invoiceStatus);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());

        // Update the invoiceStatus
        InvoiceStatus updatedInvoiceStatus = invoiceStatusRepository.findById(invoiceStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvoiceStatus are not directly saved in db
        em.detach(updatedInvoiceStatus);
        updatedInvoiceStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(updatedInvoiceStatus);

        restInvoiceStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceStatusToMatchAllProperties(updatedInvoiceStatus);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<InvoiceStatus> invoiceStatusSearchList = Streamable.of(invoiceStatusSearchRepository.findAll()).toList();
                InvoiceStatus testInvoiceStatusSearch = invoiceStatusSearchList.get(searchDatabaseSizeAfter - 1);

                assertInvoiceStatusAllPropertiesEquals(testInvoiceStatusSearch, updatedInvoiceStatus);
            });
    }

    @Test
    @Transactional
    void putNonExistingInvoiceStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        invoiceStatus.setId(longCount.incrementAndGet());

        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoiceStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        invoiceStatus.setId(longCount.incrementAndGet());

        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(invoiceStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoiceStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        invoiceStatus.setId(longCount.incrementAndGet());

        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceStatusWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceStatus using partial update
        InvoiceStatus partialUpdatedInvoiceStatus = new InvoiceStatus();
        partialUpdatedInvoiceStatus.setId(invoiceStatus.getId());

        partialUpdatedInvoiceStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);

        restInvoiceStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceStatus))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInvoiceStatus, invoiceStatus),
            getPersistedInvoiceStatus(invoiceStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateInvoiceStatusWithPatch() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoiceStatus using partial update
        InvoiceStatus partialUpdatedInvoiceStatus = new InvoiceStatus();
        partialUpdatedInvoiceStatus.setId(invoiceStatus.getId());

        partialUpdatedInvoiceStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);

        restInvoiceStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoiceStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoiceStatus))
            )
            .andExpect(status().isOk());

        // Validate the InvoiceStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceStatusUpdatableFieldsEquals(partialUpdatedInvoiceStatus, getPersistedInvoiceStatus(partialUpdatedInvoiceStatus));
    }

    @Test
    @Transactional
    void patchNonExistingInvoiceStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        invoiceStatus.setId(longCount.incrementAndGet());

        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceStatusDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoiceStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        invoiceStatus.setId(longCount.incrementAndGet());

        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoiceStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        invoiceStatus.setId(longCount.incrementAndGet());

        // Create the InvoiceStatus
        InvoiceStatusDTO invoiceStatusDTO = invoiceStatusMapper.toDto(invoiceStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(invoiceStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InvoiceStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInvoiceStatus() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);
        invoiceStatusRepository.save(invoiceStatus);
        invoiceStatusSearchRepository.save(invoiceStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the invoiceStatus
        restInvoiceStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoiceStatus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(invoiceStatusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInvoiceStatus() throws Exception {
        // Initialize the database
        insertedInvoiceStatus = invoiceStatusRepository.saveAndFlush(invoiceStatus);
        invoiceStatusSearchRepository.save(invoiceStatus);

        // Search the invoiceStatus
        restInvoiceStatusMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + invoiceStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoiceStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    protected long getRepositoryCount() {
        return invoiceStatusRepository.count();
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

    protected InvoiceStatus getPersistedInvoiceStatus(InvoiceStatus invoiceStatus) {
        return invoiceStatusRepository.findById(invoiceStatus.getId()).orElseThrow();
    }

    protected void assertPersistedInvoiceStatusToMatchAllProperties(InvoiceStatus expectedInvoiceStatus) {
        assertInvoiceStatusAllPropertiesEquals(expectedInvoiceStatus, getPersistedInvoiceStatus(expectedInvoiceStatus));
    }

    protected void assertPersistedInvoiceStatusToMatchUpdatableProperties(InvoiceStatus expectedInvoiceStatus) {
        assertInvoiceStatusAllUpdatablePropertiesEquals(expectedInvoiceStatus, getPersistedInvoiceStatus(expectedInvoiceStatus));
    }
}
