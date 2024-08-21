package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ConversationAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Conversation;
import com.foursquare.server.repository.ConversationRepository;
import com.foursquare.server.service.dto.ConversationDTO;
import com.foursquare.server.service.mapper.ConversationMapper;
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
 * Integration tests for the {@link ConversationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConversationResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/conversations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConversationMockMvc;

    private Conversation conversation;

    private Conversation insertedConversation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conversation createEntity(EntityManager em) {
        Conversation conversation = new Conversation().title(DEFAULT_TITLE);
        return conversation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conversation createUpdatedEntity(EntityManager em) {
        Conversation conversation = new Conversation().title(UPDATED_TITLE);
        return conversation;
    }

    @BeforeEach
    public void initTest() {
        conversation = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedConversation != null) {
            conversationRepository.delete(insertedConversation);
            insertedConversation = null;
        }
    }

    @Test
    @Transactional
    void createConversation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);
        var returnedConversationDTO = om.readValue(
            restConversationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conversationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConversationDTO.class
        );

        // Validate the Conversation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConversation = conversationMapper.toEntity(returnedConversationDTO);
        assertConversationUpdatableFieldsEquals(returnedConversation, getPersistedConversation(returnedConversation));

        insertedConversation = returnedConversation;
    }

    @Test
    @Transactional
    void createConversationWithExistingId() throws Exception {
        // Create the Conversation with an existing ID
        insertedConversation = conversationRepository.saveAndFlush(conversation);
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConversationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conversationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        conversation.setTitle(null);

        // Create the Conversation, which fails.
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        restConversationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conversationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConversations() throws Exception {
        // Initialize the database
        insertedConversation = conversationRepository.saveAndFlush(conversation);

        // Get all the conversationList
        restConversationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conversation.getId().toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @Test
    @Transactional
    void getConversation() throws Exception {
        // Initialize the database
        insertedConversation = conversationRepository.saveAndFlush(conversation);

        // Get the conversation
        restConversationMockMvc
            .perform(get(ENTITY_API_URL_ID, conversation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(conversation.getId().toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    void getNonExistingConversation() throws Exception {
        // Get the conversation
        restConversationMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConversation() throws Exception {
        // Initialize the database
        insertedConversation = conversationRepository.saveAndFlush(conversation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conversation
        Conversation updatedConversation = conversationRepository.findById(conversation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConversation are not directly saved in db
        em.detach(updatedConversation);
        updatedConversation.title(UPDATED_TITLE);
        ConversationDTO conversationDTO = conversationMapper.toDto(updatedConversation);

        restConversationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conversationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conversationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConversationToMatchAllProperties(updatedConversation);
    }

    @Test
    @Transactional
    void putNonExistingConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conversation.setId(UUID.randomUUID());

        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConversationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conversationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conversation.setId(UUID.randomUUID());

        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConversationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conversation.setId(UUID.randomUUID());

        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConversationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conversationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConversationWithPatch() throws Exception {
        // Initialize the database
        insertedConversation = conversationRepository.saveAndFlush(conversation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conversation using partial update
        Conversation partialUpdatedConversation = new Conversation();
        partialUpdatedConversation.setId(conversation.getId());

        partialUpdatedConversation.title(UPDATED_TITLE);

        restConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConversation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConversation))
            )
            .andExpect(status().isOk());

        // Validate the Conversation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConversationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConversation, conversation),
            getPersistedConversation(conversation)
        );
    }

    @Test
    @Transactional
    void fullUpdateConversationWithPatch() throws Exception {
        // Initialize the database
        insertedConversation = conversationRepository.saveAndFlush(conversation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conversation using partial update
        Conversation partialUpdatedConversation = new Conversation();
        partialUpdatedConversation.setId(conversation.getId());

        partialUpdatedConversation.title(UPDATED_TITLE);

        restConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConversation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConversation))
            )
            .andExpect(status().isOk());

        // Validate the Conversation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConversationUpdatableFieldsEquals(partialUpdatedConversation, getPersistedConversation(partialUpdatedConversation));
    }

    @Test
    @Transactional
    void patchNonExistingConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conversation.setId(UUID.randomUUID());

        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, conversationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conversation.setId(UUID.randomUUID());

        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConversationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conversationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConversation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        conversation.setId(UUID.randomUUID());

        // Create the Conversation
        ConversationDTO conversationDTO = conversationMapper.toDto(conversation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConversationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(conversationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conversation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConversation() throws Exception {
        // Initialize the database
        insertedConversation = conversationRepository.saveAndFlush(conversation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the conversation
        restConversationMockMvc
            .perform(delete(ENTITY_API_URL_ID, conversation.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return conversationRepository.count();
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

    protected Conversation getPersistedConversation(Conversation conversation) {
        return conversationRepository.findById(conversation.getId()).orElseThrow();
    }

    protected void assertPersistedConversationToMatchAllProperties(Conversation expectedConversation) {
        assertConversationAllPropertiesEquals(expectedConversation, getPersistedConversation(expectedConversation));
    }

    protected void assertPersistedConversationToMatchUpdatableProperties(Conversation expectedConversation) {
        assertConversationAllUpdatablePropertiesEquals(expectedConversation, getPersistedConversation(expectedConversation));
    }
}
