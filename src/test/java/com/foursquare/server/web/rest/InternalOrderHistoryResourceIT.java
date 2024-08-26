package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.InternalOrderHistoryAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.InternalOrder;
import com.foursquare.server.domain.InternalOrderHistory;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.repository.InternalOrderHistoryRepository;
import com.foursquare.server.service.InternalOrderHistoryService;
import com.foursquare.server.service.dto.InternalOrderHistoryDTO;
import com.foursquare.server.service.mapper.InternalOrderHistoryMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InternalOrderHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InternalOrderHistoryResourceIT {

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/internal-order-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InternalOrderHistoryRepository internalOrderHistoryRepository;

    @Mock
    private InternalOrderHistoryRepository internalOrderHistoryRepositoryMock;

    @Autowired
    private InternalOrderHistoryMapper internalOrderHistoryMapper;

    @Mock
    private InternalOrderHistoryService internalOrderHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInternalOrderHistoryMockMvc;

    private InternalOrderHistory internalOrderHistory;

    private InternalOrderHistory insertedInternalOrderHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalOrderHistory createEntity(EntityManager em) {
        InternalOrderHistory internalOrderHistory = new InternalOrderHistory().note(DEFAULT_NOTE);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        internalOrderHistory.setStatus(orderStatus);
        // Add required entity
        InternalOrder internalOrder;
        if (TestUtil.findAll(em, InternalOrder.class).isEmpty()) {
            internalOrder = InternalOrderResourceIT.createEntity(em);
            em.persist(internalOrder);
            em.flush();
        } else {
            internalOrder = TestUtil.findAll(em, InternalOrder.class).get(0);
        }
        internalOrderHistory.setOrder(internalOrder);
        return internalOrderHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalOrderHistory createUpdatedEntity(EntityManager em) {
        InternalOrderHistory internalOrderHistory = new InternalOrderHistory().note(UPDATED_NOTE);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createUpdatedEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        internalOrderHistory.setStatus(orderStatus);
        // Add required entity
        InternalOrder internalOrder;
        if (TestUtil.findAll(em, InternalOrder.class).isEmpty()) {
            internalOrder = InternalOrderResourceIT.createUpdatedEntity(em);
            em.persist(internalOrder);
            em.flush();
        } else {
            internalOrder = TestUtil.findAll(em, InternalOrder.class).get(0);
        }
        internalOrderHistory.setOrder(internalOrder);
        return internalOrderHistory;
    }

    @BeforeEach
    public void initTest() {
        internalOrderHistory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInternalOrderHistory != null) {
            internalOrderHistoryRepository.delete(insertedInternalOrderHistory);
            insertedInternalOrderHistory = null;
        }
    }

    @Test
    @Transactional
    void createInternalOrderHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);
        var returnedInternalOrderHistoryDTO = om.readValue(
            restInternalOrderHistoryMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderHistoryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InternalOrderHistoryDTO.class
        );

        // Validate the InternalOrderHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInternalOrderHistory = internalOrderHistoryMapper.toEntity(returnedInternalOrderHistoryDTO);
        assertInternalOrderHistoryUpdatableFieldsEquals(
            returnedInternalOrderHistory,
            getPersistedInternalOrderHistory(returnedInternalOrderHistory)
        );

        insertedInternalOrderHistory = returnedInternalOrderHistory;
    }

    @Test
    @Transactional
    void createInternalOrderHistoryWithExistingId() throws Exception {
        // Create the InternalOrderHistory with an existing ID
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInternalOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInternalOrderHistories() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get all the internalOrderHistoryList
        restInternalOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalOrderHistory.getId().toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInternalOrderHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(internalOrderHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInternalOrderHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(internalOrderHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInternalOrderHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(internalOrderHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInternalOrderHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(internalOrderHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInternalOrderHistory() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get the internalOrderHistory
        restInternalOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, internalOrderHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(internalOrderHistory.getId().toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getInternalOrderHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        UUID id = internalOrderHistory.getId();

        defaultInternalOrderHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get all the internalOrderHistoryList where note equals to
        defaultInternalOrderHistoryFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get all the internalOrderHistoryList where note in
        defaultInternalOrderHistoryFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get all the internalOrderHistoryList where note is not null
        defaultInternalOrderHistoryFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get all the internalOrderHistoryList where note contains
        defaultInternalOrderHistoryFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        // Get all the internalOrderHistoryList where note does not contain
        defaultInternalOrderHistoryFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByStatusIsEqualToSomething() throws Exception {
        OrderStatus status;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);
            status = OrderStatusResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        em.persist(status);
        em.flush();
        internalOrderHistory.setStatus(status);
        internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);
        Long statusId = status.getId();
        // Get all the internalOrderHistoryList where status equals to statusId
        defaultInternalOrderHistoryShouldBeFound("statusId.equals=" + statusId);

        // Get all the internalOrderHistoryList where status equals to (statusId + 1)
        defaultInternalOrderHistoryShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllInternalOrderHistoriesByOrderIsEqualToSomething() throws Exception {
        InternalOrder order;
        if (TestUtil.findAll(em, InternalOrder.class).isEmpty()) {
            internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);
            order = InternalOrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, InternalOrder.class).get(0);
        }
        em.persist(order);
        em.flush();
        internalOrderHistory.setOrder(order);
        internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);
        UUID orderId = order.getId();
        // Get all the internalOrderHistoryList where order equals to orderId
        defaultInternalOrderHistoryShouldBeFound("orderId.equals=" + orderId);

        // Get all the internalOrderHistoryList where order equals to UUID.randomUUID()
        defaultInternalOrderHistoryShouldNotBeFound("orderId.equals=" + UUID.randomUUID());
    }

    private void defaultInternalOrderHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInternalOrderHistoryShouldBeFound(shouldBeFound);
        defaultInternalOrderHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInternalOrderHistoryShouldBeFound(String filter) throws Exception {
        restInternalOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalOrderHistory.getId().toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restInternalOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInternalOrderHistoryShouldNotBeFound(String filter) throws Exception {
        restInternalOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInternalOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInternalOrderHistory() throws Exception {
        // Get the internalOrderHistory
        restInternalOrderHistoryMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInternalOrderHistory() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrderHistory
        InternalOrderHistory updatedInternalOrderHistory = internalOrderHistoryRepository
            .findById(internalOrderHistory.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInternalOrderHistory are not directly saved in db
        em.detach(updatedInternalOrderHistory);
        updatedInternalOrderHistory.note(UPDATED_NOTE);
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(updatedInternalOrderHistory);

        restInternalOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalOrderHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInternalOrderHistoryToMatchAllProperties(updatedInternalOrderHistory);
    }

    @Test
    @Transactional
    void putNonExistingInternalOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderHistory.setId(UUID.randomUUID());

        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalOrderHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInternalOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderHistory.setId(UUID.randomUUID());

        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInternalOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderHistory.setId(UUID.randomUUID());

        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInternalOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrderHistory using partial update
        InternalOrderHistory partialUpdatedInternalOrderHistory = new InternalOrderHistory();
        partialUpdatedInternalOrderHistory.setId(internalOrderHistory.getId());

        restInternalOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalOrderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInternalOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrderHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInternalOrderHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInternalOrderHistory, internalOrderHistory),
            getPersistedInternalOrderHistory(internalOrderHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateInternalOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrderHistory using partial update
        InternalOrderHistory partialUpdatedInternalOrderHistory = new InternalOrderHistory();
        partialUpdatedInternalOrderHistory.setId(internalOrderHistory.getId());

        partialUpdatedInternalOrderHistory.note(UPDATED_NOTE);

        restInternalOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalOrderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInternalOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrderHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInternalOrderHistoryUpdatableFieldsEquals(
            partialUpdatedInternalOrderHistory,
            getPersistedInternalOrderHistory(partialUpdatedInternalOrderHistory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInternalOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderHistory.setId(UUID.randomUUID());

        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, internalOrderHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(internalOrderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInternalOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderHistory.setId(UUID.randomUUID());

        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(internalOrderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInternalOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderHistory.setId(UUID.randomUUID());

        // Create the InternalOrderHistory
        InternalOrderHistoryDTO internalOrderHistoryDTO = internalOrderHistoryMapper.toDto(internalOrderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(internalOrderHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalOrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInternalOrderHistory() throws Exception {
        // Initialize the database
        insertedInternalOrderHistory = internalOrderHistoryRepository.saveAndFlush(internalOrderHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the internalOrderHistory
        restInternalOrderHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, internalOrderHistory.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return internalOrderHistoryRepository.count();
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

    protected InternalOrderHistory getPersistedInternalOrderHistory(InternalOrderHistory internalOrderHistory) {
        return internalOrderHistoryRepository.findById(internalOrderHistory.getId()).orElseThrow();
    }

    protected void assertPersistedInternalOrderHistoryToMatchAllProperties(InternalOrderHistory expectedInternalOrderHistory) {
        assertInternalOrderHistoryAllPropertiesEquals(
            expectedInternalOrderHistory,
            getPersistedInternalOrderHistory(expectedInternalOrderHistory)
        );
    }

    protected void assertPersistedInternalOrderHistoryToMatchUpdatableProperties(InternalOrderHistory expectedInternalOrderHistory) {
        assertInternalOrderHistoryAllUpdatablePropertiesEquals(
            expectedInternalOrderHistory,
            getPersistedInternalOrderHistory(expectedInternalOrderHistory)
        );
    }
}
