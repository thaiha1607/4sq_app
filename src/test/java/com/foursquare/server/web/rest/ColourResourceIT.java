package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ColourAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Colour;
import com.foursquare.server.repository.ColourRepository;
import com.foursquare.server.service.dto.ColourDTO;
import com.foursquare.server.service.mapper.ColourMapper;
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
 * Integration tests for the {@link ColourResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ColourResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HEX_CODE = "#0CF";
    private static final String UPDATED_HEX_CODE = "#7d7e0B";

    private static final String ENTITY_API_URL = "/api/colours";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ColourRepository colourRepository;

    @Autowired
    private ColourMapper colourMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restColourMockMvc;

    private Colour colour;

    private Colour insertedColour;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colour createEntity(EntityManager em) {
        Colour colour = new Colour().name(DEFAULT_NAME).hexCode(DEFAULT_HEX_CODE);
        return colour;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colour createUpdatedEntity(EntityManager em) {
        Colour colour = new Colour().name(UPDATED_NAME).hexCode(UPDATED_HEX_CODE);
        return colour;
    }

    @BeforeEach
    public void initTest() {
        colour = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedColour != null) {
            colourRepository.delete(insertedColour);
            insertedColour = null;
        }
    }

    @Test
    @Transactional
    void createColour() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);
        var returnedColourDTO = om.readValue(
            restColourMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ColourDTO.class
        );

        // Validate the Colour in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedColour = colourMapper.toEntity(returnedColourDTO);
        assertColourUpdatableFieldsEquals(returnedColour, getPersistedColour(returnedColour));

        insertedColour = returnedColour;
    }

    @Test
    @Transactional
    void createColourWithExistingId() throws Exception {
        // Create the Colour with an existing ID
        insertedColour = colourRepository.saveAndFlush(colour);
        ColourDTO colourDTO = colourMapper.toDto(colour);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restColourMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        colour.setName(null);

        // Create the Colour, which fails.
        ColourDTO colourDTO = colourMapper.toDto(colour);

        restColourMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHexCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        colour.setHexCode(null);

        // Create the Colour, which fails.
        ColourDTO colourDTO = colourMapper.toDto(colour);

        restColourMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllColours() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList
        restColourMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colour.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hexCode").value(hasItem(DEFAULT_HEX_CODE)));
    }

    @Test
    @Transactional
    void getColour() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get the colour
        restColourMockMvc
            .perform(get(ENTITY_API_URL_ID, colour.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(colour.getId().toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hexCode").value(DEFAULT_HEX_CODE));
    }

    @Test
    @Transactional
    void getColoursByIdFiltering() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        UUID id = colour.getId();

        defaultColourFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllColoursByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where name equals to
        defaultColourFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColoursByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where name in
        defaultColourFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColoursByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where name is not null
        defaultColourFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllColoursByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where name contains
        defaultColourFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllColoursByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where name does not contain
        defaultColourFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllColoursByHexCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where hexCode equals to
        defaultColourFiltering("hexCode.equals=" + DEFAULT_HEX_CODE, "hexCode.equals=" + UPDATED_HEX_CODE);
    }

    @Test
    @Transactional
    void getAllColoursByHexCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where hexCode in
        defaultColourFiltering("hexCode.in=" + DEFAULT_HEX_CODE + "," + UPDATED_HEX_CODE, "hexCode.in=" + UPDATED_HEX_CODE);
    }

    @Test
    @Transactional
    void getAllColoursByHexCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where hexCode is not null
        defaultColourFiltering("hexCode.specified=true", "hexCode.specified=false");
    }

    @Test
    @Transactional
    void getAllColoursByHexCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where hexCode contains
        defaultColourFiltering("hexCode.contains=" + DEFAULT_HEX_CODE, "hexCode.contains=" + UPDATED_HEX_CODE);
    }

    @Test
    @Transactional
    void getAllColoursByHexCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        // Get all the colourList where hexCode does not contain
        defaultColourFiltering("hexCode.doesNotContain=" + UPDATED_HEX_CODE, "hexCode.doesNotContain=" + DEFAULT_HEX_CODE);
    }

    private void defaultColourFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultColourShouldBeFound(shouldBeFound);
        defaultColourShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultColourShouldBeFound(String filter) throws Exception {
        restColourMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colour.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hexCode").value(hasItem(DEFAULT_HEX_CODE)));

        // Check, that the count call also returns 1
        restColourMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultColourShouldNotBeFound(String filter) throws Exception {
        restColourMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restColourMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingColour() throws Exception {
        // Get the colour
        restColourMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingColour() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the colour
        Colour updatedColour = colourRepository.findById(colour.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedColour are not directly saved in db
        em.detach(updatedColour);
        updatedColour.name(UPDATED_NAME).hexCode(UPDATED_HEX_CODE);
        ColourDTO colourDTO = colourMapper.toDto(updatedColour);

        restColourMockMvc
            .perform(
                put(ENTITY_API_URL_ID, colourDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO))
            )
            .andExpect(status().isOk());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedColourToMatchAllProperties(updatedColour);
    }

    @Test
    @Transactional
    void putNonExistingColour() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colour.setId(UUID.randomUUID());

        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColourMockMvc
            .perform(
                put(ENTITY_API_URL_ID, colourDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchColour() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colour.setId(UUID.randomUUID());

        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColourMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamColour() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colour.setId(UUID.randomUUID());

        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColourMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colourDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateColourWithPatch() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the colour using partial update
        Colour partialUpdatedColour = new Colour();
        partialUpdatedColour.setId(colour.getId());

        restColourMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColour.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedColour))
            )
            .andExpect(status().isOk());

        // Validate the Colour in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertColourUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedColour, colour), getPersistedColour(colour));
    }

    @Test
    @Transactional
    void fullUpdateColourWithPatch() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the colour using partial update
        Colour partialUpdatedColour = new Colour();
        partialUpdatedColour.setId(colour.getId());

        partialUpdatedColour.name(UPDATED_NAME).hexCode(UPDATED_HEX_CODE);

        restColourMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColour.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedColour))
            )
            .andExpect(status().isOk());

        // Validate the Colour in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertColourUpdatableFieldsEquals(partialUpdatedColour, getPersistedColour(partialUpdatedColour));
    }

    @Test
    @Transactional
    void patchNonExistingColour() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colour.setId(UUID.randomUUID());

        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColourMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, colourDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(colourDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchColour() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colour.setId(UUID.randomUUID());

        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColourMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(colourDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamColour() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        colour.setId(UUID.randomUUID());

        // Create the Colour
        ColourDTO colourDTO = colourMapper.toDto(colour);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColourMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(colourDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Colour in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteColour() throws Exception {
        // Initialize the database
        insertedColour = colourRepository.saveAndFlush(colour);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the colour
        restColourMockMvc
            .perform(delete(ENTITY_API_URL_ID, colour.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return colourRepository.count();
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

    protected Colour getPersistedColour(Colour colour) {
        return colourRepository.findById(colour.getId()).orElseThrow();
    }

    protected void assertPersistedColourToMatchAllProperties(Colour expectedColour) {
        assertColourAllPropertiesEquals(expectedColour, getPersistedColour(expectedColour));
    }

    protected void assertPersistedColourToMatchUpdatableProperties(Colour expectedColour) {
        assertColourAllUpdatablePropertiesEquals(expectedColour, getPersistedColour(expectedColour));
    }
}
