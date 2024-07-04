package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ParticipantAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Participant;
import com.foursquare.server.domain.User;
import com.foursquare.server.repository.ParticipantRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.repository.search.ParticipantSearchRepository;
import com.foursquare.server.service.ParticipantService;
import com.foursquare.server.service.dto.ParticipantDTO;
import com.foursquare.server.service.mapper.ParticipantMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ParticipantResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ParticipantResourceIT {

    private static final Boolean DEFAULT_IS_ADMIN = false;
    private static final Boolean UPDATED_IS_ADMIN = true;

    private static final String ENTITY_API_URL = "/api/participants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/participants/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ParticipantRepository participantRepositoryMock;

    @Autowired
    private ParticipantMapper participantMapper;

    @Mock
    private ParticipantService participantServiceMock;

    @Autowired
    private ParticipantSearchRepository participantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantMockMvc;

    private Participant participant;

    private Participant insertedParticipant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createEntity(EntityManager em) {
        Participant participant = new Participant().isAdmin(DEFAULT_IS_ADMIN);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        participant.setUser(user);
        return participant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createUpdatedEntity(EntityManager em) {
        Participant participant = new Participant().isAdmin(UPDATED_IS_ADMIN);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        participant.setUser(user);
        return participant;
    }

    @BeforeEach
    public void initTest() {
        participant = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedParticipant != null) {
            participantRepository.delete(insertedParticipant);
            participantSearchRepository.delete(insertedParticipant);
            insertedParticipant = null;
        }
    }

    @Test
    @Transactional
    void createParticipant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);
        var returnedParticipantDTO = om.readValue(
            restParticipantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParticipantDTO.class
        );

        // Validate the Participant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParticipant = participantMapper.toEntity(returnedParticipantDTO);
        assertParticipantUpdatableFieldsEquals(returnedParticipant, getPersistedParticipant(returnedParticipant));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedParticipant = returnedParticipant;
    }

    @Test
    @Transactional
    void createParticipantWithExistingId() throws Exception {
        // Create the Participant with an existing ID
        insertedParticipant = participantRepository.saveAndFlush(participant);
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllParticipants() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().toString())))
            .andExpect(jsonPath("$.[*].isAdmin").value(hasItem(DEFAULT_IS_ADMIN.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParticipantsWithEagerRelationshipsIsEnabled() throws Exception {
        when(participantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restParticipantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(participantServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParticipantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(participantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restParticipantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(participantRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get the participant
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL_ID, participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participant.getId().toString()))
            .andExpect(jsonPath("$.isAdmin").value(DEFAULT_IS_ADMIN.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingParticipant() throws Exception {
        // Get the participant
        restParticipantMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantSearchRepository.save(participant);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());

        // Update the participant
        Participant updatedParticipant = participantRepository.findById(participant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParticipant are not directly saved in db
        em.detach(updatedParticipant);
        updatedParticipant.isAdmin(UPDATED_IS_ADMIN);
        ParticipantDTO participantDTO = participantMapper.toDto(updatedParticipant);

        restParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParticipantToMatchAllProperties(updatedParticipant);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Participant> participantSearchList = Streamable.of(participantSearchRepository.findAll()).toList();
                Participant testParticipantSearch = participantSearchList.get(searchDatabaseSizeAfter - 1);

                assertParticipantAllPropertiesEquals(testParticipantSearch, updatedParticipant);
            });
    }

    @Test
    @Transactional
    void putNonExistingParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        participant.setId(UUID.randomUUID());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        participant.setId(UUID.randomUUID());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        participant.setId(UUID.randomUUID());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateParticipantWithPatch() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participant using partial update
        Participant partialUpdatedParticipant = new Participant();
        partialUpdatedParticipant.setId(participant.getId());

        partialUpdatedParticipant.isAdmin(UPDATED_IS_ADMIN);

        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipant))
            )
            .andExpect(status().isOk());

        // Validate the Participant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParticipant, participant),
            getPersistedParticipant(participant)
        );
    }

    @Test
    @Transactional
    void fullUpdateParticipantWithPatch() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participant using partial update
        Participant partialUpdatedParticipant = new Participant();
        partialUpdatedParticipant.setId(participant.getId());

        partialUpdatedParticipant.isAdmin(UPDATED_IS_ADMIN);

        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipant))
            )
            .andExpect(status().isOk());

        // Validate the Participant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantUpdatableFieldsEquals(partialUpdatedParticipant, getPersistedParticipant(partialUpdatedParticipant));
    }

    @Test
    @Transactional
    void patchNonExistingParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        participant.setId(UUID.randomUUID());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, participantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        participant.setId(UUID.randomUUID());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        participant.setId(UUID.randomUUID());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);
        participantRepository.save(participant);
        participantSearchRepository.save(participant);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the participant
        restParticipantMockMvc
            .perform(delete(ENTITY_API_URL_ID, participant.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(participantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);
        participantSearchRepository.save(participant);

        // Search the participant
        restParticipantMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().toString())))
            .andExpect(jsonPath("$.[*].isAdmin").value(hasItem(DEFAULT_IS_ADMIN.booleanValue())));
    }

    protected long getRepositoryCount() {
        return participantRepository.count();
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

    protected Participant getPersistedParticipant(Participant participant) {
        return participantRepository.findById(participant.getId()).orElseThrow();
    }

    protected void assertPersistedParticipantToMatchAllProperties(Participant expectedParticipant) {
        assertParticipantAllPropertiesEquals(expectedParticipant, getPersistedParticipant(expectedParticipant));
    }

    protected void assertPersistedParticipantToMatchUpdatableProperties(Participant expectedParticipant) {
        assertParticipantAllUpdatablePropertiesEquals(expectedParticipant, getPersistedParticipant(expectedParticipant));
    }
}
