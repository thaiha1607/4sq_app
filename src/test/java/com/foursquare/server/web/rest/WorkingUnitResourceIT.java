package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.WorkingUnitAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Address;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.domain.enumeration.WorkingUnitType;
import com.foursquare.server.repository.WorkingUnitRepository;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import com.foursquare.server.service.mapper.WorkingUnitMapper;
import jakarta.persistence.EntityManager;
import java.util.UUID;
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

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkingUnitRepository workingUnitRepository;

    @Autowired
    private WorkingUnitMapper workingUnitMapper;

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
            insertedWorkingUnit = null;
        }
    }

    @Test
    @Transactional
    void createWorkingUnit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
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

        insertedWorkingUnit = returnedWorkingUnit;
    }

    @Test
    @Transactional
    void createWorkingUnitWithExistingId() throws Exception {
        // Create the WorkingUnit with an existing ID
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkingUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workingUnit.setName(null);

        // Create the WorkingUnit, which fails.
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        restWorkingUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workingUnit.setType(null);

        // Create the WorkingUnit, which fails.
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        restWorkingUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
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
    void getWorkingUnitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        UUID id = workingUnit.getId();

        defaultWorkingUnitFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where name equals to
        defaultWorkingUnitFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where name in
        defaultWorkingUnitFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where name is not null
        defaultWorkingUnitFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where name contains
        defaultWorkingUnitFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where name does not contain
        defaultWorkingUnitFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where type equals to
        defaultWorkingUnitFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where type in
        defaultWorkingUnitFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where type is not null
        defaultWorkingUnitFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByImageUriIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where imageUri equals to
        defaultWorkingUnitFiltering("imageUri.equals=" + DEFAULT_IMAGE_URI, "imageUri.equals=" + UPDATED_IMAGE_URI);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByImageUriIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where imageUri in
        defaultWorkingUnitFiltering("imageUri.in=" + DEFAULT_IMAGE_URI + "," + UPDATED_IMAGE_URI, "imageUri.in=" + UPDATED_IMAGE_URI);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByImageUriIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where imageUri is not null
        defaultWorkingUnitFiltering("imageUri.specified=true", "imageUri.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByImageUriContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where imageUri contains
        defaultWorkingUnitFiltering("imageUri.contains=" + DEFAULT_IMAGE_URI, "imageUri.contains=" + UPDATED_IMAGE_URI);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByImageUriNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        // Get all the workingUnitList where imageUri does not contain
        defaultWorkingUnitFiltering("imageUri.doesNotContain=" + UPDATED_IMAGE_URI, "imageUri.doesNotContain=" + DEFAULT_IMAGE_URI);
    }

    @Test
    @Transactional
    void getAllWorkingUnitsByAddressIsEqualToSomething() throws Exception {
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            workingUnitRepository.saveAndFlush(workingUnit);
            address = AddressResourceIT.createEntity(em);
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        em.persist(address);
        em.flush();
        workingUnit.setAddress(address);
        workingUnitRepository.saveAndFlush(workingUnit);
        UUID addressId = address.getId();
        // Get all the workingUnitList where address equals to addressId
        defaultWorkingUnitShouldBeFound("addressId.equals=" + addressId);

        // Get all the workingUnitList where address equals to UUID.randomUUID()
        defaultWorkingUnitShouldNotBeFound("addressId.equals=" + UUID.randomUUID());
    }

    private void defaultWorkingUnitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWorkingUnitShouldBeFound(shouldBeFound);
        defaultWorkingUnitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkingUnitShouldBeFound(String filter) throws Exception {
        restWorkingUnitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workingUnit.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].imageUri").value(hasItem(DEFAULT_IMAGE_URI)));

        // Check, that the count call also returns 1
        restWorkingUnitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkingUnitShouldNotBeFound(String filter) throws Exception {
        restWorkingUnitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkingUnitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
    }

    @Test
    @Transactional
    void putNonExistingWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
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
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkingUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workingUnit.setId(UUID.randomUUID());

        // Create the WorkingUnit
        WorkingUnitDTO workingUnitDTO = workingUnitMapper.toDto(workingUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkingUnitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(workingUnitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkingUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkingUnit() throws Exception {
        // Initialize the database
        insertedWorkingUnit = workingUnitRepository.saveAndFlush(workingUnit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workingUnit
        restWorkingUnitMockMvc
            .perform(delete(ENTITY_API_URL_ID, workingUnit.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
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
