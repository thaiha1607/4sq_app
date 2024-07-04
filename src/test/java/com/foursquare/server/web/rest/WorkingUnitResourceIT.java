package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.WorkingUnitAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.domain.enumeration.WorkingUnitType;
import com.foursquare.server.repository.WorkingUnitRepository;
import com.foursquare.server.repository.search.WorkingUnitSearchRepository;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import com.foursquare.server.service.mapper.WorkingUnitMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
 * Integration tests for the {@link WorkingUnitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkingUnitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final WorkingUnitType DEFAULT_TYPE = WorkingUnitType.WAREHOUSE;
    private static final WorkingUnitType UPDATED_TYPE = WorkingUnitType.OFFICE;

    private static final String DEFAULT_IMAGE_URI = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URI = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/working-units";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/working-units/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkingUnitRepository workingUnitRepository;

    @Autowired
    private WorkingUnitMapper workingUnitMapper;

    @Autowired
    private WorkingUnitSearchRepository workingUnitSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkingUnitMockMvc;

    private WorkingUnit workingUnit;

    private WorkingUnit insertedWorkingUnit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkingUnit createEntity(EntityManager em) {
        WorkingUnit workingUnit = new WorkingUnit().name(DEFAULT_NAME).type(DEFAULT_TYPE).imageUri(DEFAULT_IMAGE_URI);
        return workingUnit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkingUnit createUpdatedEntity(EntityManager em) {
        WorkingUnit workingUnit = new WorkingUnit().name(UPDATED_NAME).type(UPDATED_TYPE).imageUri(UPDATED_IMAGE_URI);
        return workingUnit;
    }

    @BeforeEach
    public void initTest() {
        workingUnit = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedWorkingUnit != null) {
            workingUnitRepository.delete(insertedWorkingUnit);
            workingUnitSearchRepository.delete(insertedWorkingUnit);
            insertedWorkingUnit = null;
        }
    }

    @Test
    @Transactional
    void createWorkingUnit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);
        var returnedWorkingUnitDTO = om.readValue(
            restWorkingUnitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WorkingUnitDTO.class
        );

        // Validate the WorkingUnit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWorkingUnit = workingUnitMapper.toEntity(returnedWorkingUnitDTO);
        assertWorkingUnitUpdatableFieldsEquals(returnedWorkingUnit, getPersistedWorkingUnit(returnedWorkingUnit));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedWorkingUnit = returnedWorkingUnit;
    }

    @Test
    @Transactional
    void createWorkingUnitWithExistingId() throws Exception {
        // Create the WorkingUnit with an existing ID
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkingUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        // set the field null
        workingUnit.setName(null);

        // Create the WorkingUnit, which fails.
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        restWorkingUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        // set the field null
        workingUnit.setType(null);

        // Create the WorkingUnit, which fails.
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        restWorkingUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllWorkingUnits() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList
        restWorkingUnitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workingUnit.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].imageUri").value(hasItem(DEFAULT_IMAGE_URI)));
    }

    @Test
    @Transactional
    void getWorkingUnit() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get the workingUnit
        restWorkingUnitMockMvc
            .perform(get(ENTITY_API_URL_ID, workingUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workingUnit.getId().toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.imageUri").value(DEFAULT_IMAGE_URI));
    }

    @Test
    @Transactional
    void getNonExistingWorkingUnit() throws Exception {
        // Get the workingUnit
        restWorkingUnitMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkingUnit() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        workingUnitSearchRepository.save(workingUnit);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());

        // Update the workingUnit
        WorkingUnit updatedWorkingUnit = workingUnitRepository.findById(workingUnit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWorkingUnit are not directly saved in db
        em.detach(updatedWorkingUnit);
        updatedWorkingUnit.name(UPDATED_NAME).type(UPDATED_TYPE).imageUri(UPDATED_IMAGE_URI);
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(updatedWorkingUnit);

        restWorkingUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workingUnitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workingUnitDTO))
            )
            .andExpect(status().isOk());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkingUnitToMatchAllProperties(updatedWorkingUnit);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<WorkingUnit> workingUnitSearchList = Streamable.of(workingUnitSearchRepository.findAll()).toList();
                WorkingUnit testWorkingUnitSearch = workingUnitSearchList.get(searchDatabaseSizeAfter - 1);

                assertWorkingUnitAllPropertiesEquals(testWorkingUnitSearch, updatedWorkingUnit);
            });
    }

    @Test
    @Transactional
    void putNonExistingWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workingUnitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workingUnitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workingUnitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateWorkingUnitWithPatch() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workingUnit using partial update
        WorkingUnit partialUpdatedWorkingUnit = new WorkingUnit();
        partialUpdatedWorkingUnit.setId(workingUnit.getId());

        partialUpdatedWorkingUnit.name(UPDATED_NAME).type(UPDATED_TYPE).imageUri(UPDATED_IMAGE_URI);

        restWorkingUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkingUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkingUnit))
            )
            .andExpect(status().isOk());

        // Validate the WorkingUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkingUnitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkingUnit, workingUnit),
            getPersistedWorkingUnit(workingUnit)
        );
    }

    @Test
    @Transactional
    void fullUpdateWorkingUnitWithPatch() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workingUnit using partial update
        WorkingUnit partialUpdatedWorkingUnit = new WorkingUnit();
        partialUpdatedWorkingUnit.setId(workingUnit.getId());

        partialUpdatedWorkingUnit.name(UPDATED_NAME).type(UPDATED_TYPE).imageUri(UPDATED_IMAGE_URI);

        restWorkingUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkingUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkingUnit))
            )
            .andExpect(status().isOk());

        // Validate the WorkingUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkingUnitUpdatableFieldsEquals(partialUpdatedWorkingUnit, getPersistedWorkingUnit(partialUpdatedWorkingUnit));
    }

    @Test
    @Transactional
    void patchNonExistingWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workingUnitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workingUnitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workingUnitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteWorkingUnit() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);
        workingUnitRepository.save(workingUnit);
        workingUnitSearchRepository.save(workingUnit);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the workingUnit
        restWorkingUnitMockMvc
            .perform(delete(ENTITY_API_URL_ID, workingUnit.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workingUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchWorkingUnit() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);
        workingUnitSearchRepository.save(workingUnit);

        // Search the workingUnit
        restWorkingUnitMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + workingUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workingUnit.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].imageUri").value(hasItem(DEFAULT_IMAGE_URI)));
    }

    protected long getRepositoryCount() {
        return workingUnitRepository.count();
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

    protected WorkingUnit getPersistedWorkingUnit(WorkingUnit workingUnit) {
        return workingUnitRepository.findById(workingUnit.getId()).orElseThrow();
    }

    protected void assertPersistedWorkingUnitToMatchAllProperties(WorkingUnit expectedWorkingUnit) {
        assertWorkingUnitAllPropertiesEquals(expectedWorkingUnit, getPersistedWorkingUnit(expectedWorkingUnit));
    }

    protected void assertPersistedWorkingUnitToMatchUpdatableProperties(WorkingUnit expectedWorkingUnit) {
        assertWorkingUnitAllUpdatablePropertiesEquals(expectedWorkingUnit, getPersistedWorkingUnit(expectedWorkingUnit));
    }
}
