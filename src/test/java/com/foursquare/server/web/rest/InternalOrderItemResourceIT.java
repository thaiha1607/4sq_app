package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.InternalOrderItemAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.InternalOrderItem;
import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.repository.InternalOrderItemRepository;
import com.foursquare.server.service.dto.InternalOrderItemDTO;
import com.foursquare.server.service.mapper.InternalOrderItemMapper;
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
 * Integration tests for the {@link InternalOrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InternalOrderItemResourceIT {

    private static final Integer DEFAULT_QTY = 0;
    private static final Integer UPDATED_QTY = 1;
    private static final Integer SMALLER_QTY = 0 - 1;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/internal-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InternalOrderItemRepository internalOrderItemRepository;

    @Autowired
    private InternalOrderItemMapper internalOrderItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInternalOrderItemMockMvc;

    private InternalOrderItem internalOrderItem;

    private InternalOrderItem insertedInternalOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalOrderItem createEntity(EntityManager em) {
        InternalOrderItem internalOrderItem = new InternalOrderItem().qty(DEFAULT_QTY).note(DEFAULT_NOTE);
        // Add required entity
        OrderItem orderItem;
        if (TestUtil.findAll(em, OrderItem.class).isEmpty()) {
            orderItem = OrderItemResourceIT.createEntity(em);
            em.persist(orderItem);
            em.flush();
        } else {
            orderItem = TestUtil.findAll(em, OrderItem.class).get(0);
        }
        internalOrderItem.setOrderItem(orderItem);
        return internalOrderItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalOrderItem createUpdatedEntity(EntityManager em) {
        InternalOrderItem internalOrderItem = new InternalOrderItem().qty(UPDATED_QTY).note(UPDATED_NOTE);
        // Add required entity
        OrderItem orderItem;
        if (TestUtil.findAll(em, OrderItem.class).isEmpty()) {
            orderItem = OrderItemResourceIT.createUpdatedEntity(em);
            em.persist(orderItem);
            em.flush();
        } else {
            orderItem = TestUtil.findAll(em, OrderItem.class).get(0);
        }
        internalOrderItem.setOrderItem(orderItem);
        return internalOrderItem;
    }

    @BeforeEach
    public void initTest() {
        internalOrderItem = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInternalOrderItem != null) {
            internalOrderItemRepository.delete(insertedInternalOrderItem);
            insertedInternalOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createInternalOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);
        var returnedInternalOrderItemDTO = om.readValue(
            restInternalOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InternalOrderItemDTO.class
        );

        // Validate the InternalOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInternalOrderItem = internalOrderItemMapper.toEntity(returnedInternalOrderItemDTO);
        assertInternalOrderItemUpdatableFieldsEquals(returnedInternalOrderItem, getPersistedInternalOrderItem(returnedInternalOrderItem));

        insertedInternalOrderItem = returnedInternalOrderItem;
    }

    @Test
    @Transactional
    void createInternalOrderItemWithExistingId() throws Exception {
        // Create the InternalOrderItem with an existing ID
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInternalOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        internalOrderItem.setQty(null);

        // Create the InternalOrderItem, which fails.
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        restInternalOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInternalOrderItems() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList
        restInternalOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalOrderItem.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getInternalOrderItem() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get the internalOrderItem
        restInternalOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, internalOrderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(internalOrderItem.getId().toString()))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getInternalOrderItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        UUID id = internalOrderItem.getId();

        defaultInternalOrderItemFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty equals to
        defaultInternalOrderItemFiltering("qty.equals=" + DEFAULT_QTY, "qty.equals=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty in
        defaultInternalOrderItemFiltering("qty.in=" + DEFAULT_QTY + "," + UPDATED_QTY, "qty.in=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty is not null
        defaultInternalOrderItemFiltering("qty.specified=true", "qty.specified=false");
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty is greater than or equal to
        defaultInternalOrderItemFiltering("qty.greaterThanOrEqual=" + DEFAULT_QTY, "qty.greaterThanOrEqual=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty is less than or equal to
        defaultInternalOrderItemFiltering("qty.lessThanOrEqual=" + DEFAULT_QTY, "qty.lessThanOrEqual=" + SMALLER_QTY);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty is less than
        defaultInternalOrderItemFiltering("qty.lessThan=" + UPDATED_QTY, "qty.lessThan=" + DEFAULT_QTY);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where qty is greater than
        defaultInternalOrderItemFiltering("qty.greaterThan=" + SMALLER_QTY, "qty.greaterThan=" + DEFAULT_QTY);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where note equals to
        defaultInternalOrderItemFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where note in
        defaultInternalOrderItemFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where note is not null
        defaultInternalOrderItemFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where note contains
        defaultInternalOrderItemFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        // Get all the internalOrderItemList where note does not contain
        defaultInternalOrderItemFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrderItemsByOrderItemIsEqualToSomething() throws Exception {
        OrderItem orderItem;
        if (TestUtil.findAll(em, OrderItem.class).isEmpty()) {
            internalOrderItemRepository.saveAndFlush(internalOrderItem);
            orderItem = OrderItemResourceIT.createEntity(em);
        } else {
            orderItem = TestUtil.findAll(em, OrderItem.class).get(0);
        }
        em.persist(orderItem);
        em.flush();
        internalOrderItem.setOrderItem(orderItem);
        internalOrderItemRepository.saveAndFlush(internalOrderItem);
        UUID orderItemId = orderItem.getId();
        // Get all the internalOrderItemList where orderItem equals to orderItemId
        defaultInternalOrderItemShouldBeFound("orderItemId.equals=" + orderItemId);

        // Get all the internalOrderItemList where orderItem equals to UUID.randomUUID()
        defaultInternalOrderItemShouldNotBeFound("orderItemId.equals=" + UUID.randomUUID());
    }

    private void defaultInternalOrderItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInternalOrderItemShouldBeFound(shouldBeFound);
        defaultInternalOrderItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInternalOrderItemShouldBeFound(String filter) throws Exception {
        restInternalOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalOrderItem.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restInternalOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInternalOrderItemShouldNotBeFound(String filter) throws Exception {
        restInternalOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInternalOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInternalOrderItem() throws Exception {
        // Get the internalOrderItem
        restInternalOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInternalOrderItem() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrderItem
        InternalOrderItem updatedInternalOrderItem = internalOrderItemRepository.findById(internalOrderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInternalOrderItem are not directly saved in db
        em.detach(updatedInternalOrderItem);
        updatedInternalOrderItem.qty(UPDATED_QTY).note(UPDATED_NOTE);
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(updatedInternalOrderItem);

        restInternalOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInternalOrderItemToMatchAllProperties(updatedInternalOrderItem);
    }

    @Test
    @Transactional
    void putNonExistingInternalOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderItem.setId(UUID.randomUUID());

        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInternalOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderItem.setId(UUID.randomUUID());

        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInternalOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderItem.setId(UUID.randomUUID());

        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInternalOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrderItem using partial update
        InternalOrderItem partialUpdatedInternalOrderItem = new InternalOrderItem();
        partialUpdatedInternalOrderItem.setId(internalOrderItem.getId());

        restInternalOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInternalOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInternalOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInternalOrderItem, internalOrderItem),
            getPersistedInternalOrderItem(internalOrderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateInternalOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrderItem using partial update
        InternalOrderItem partialUpdatedInternalOrderItem = new InternalOrderItem();
        partialUpdatedInternalOrderItem.setId(internalOrderItem.getId());

        partialUpdatedInternalOrderItem.qty(UPDATED_QTY).note(UPDATED_NOTE);

        restInternalOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInternalOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInternalOrderItemUpdatableFieldsEquals(
            partialUpdatedInternalOrderItem,
            getPersistedInternalOrderItem(partialUpdatedInternalOrderItem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInternalOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderItem.setId(UUID.randomUUID());

        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, internalOrderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(internalOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInternalOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderItem.setId(UUID.randomUUID());

        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(internalOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInternalOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrderItem.setId(UUID.randomUUID());

        // Create the InternalOrderItem
        InternalOrderItemDTO internalOrderItemDTO = internalOrderItemMapper.toDto(internalOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(internalOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInternalOrderItem() throws Exception {
        // Initialize the database
        insertedInternalOrderItem = internalOrderItemRepository.saveAndFlush(internalOrderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the internalOrderItem
        restInternalOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, internalOrderItem.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return internalOrderItemRepository.count();
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

    protected InternalOrderItem getPersistedInternalOrderItem(InternalOrderItem internalOrderItem) {
        return internalOrderItemRepository.findById(internalOrderItem.getId()).orElseThrow();
    }

    protected void assertPersistedInternalOrderItemToMatchAllProperties(InternalOrderItem expectedInternalOrderItem) {
        assertInternalOrderItemAllPropertiesEquals(expectedInternalOrderItem, getPersistedInternalOrderItem(expectedInternalOrderItem));
    }

    protected void assertPersistedInternalOrderItemToMatchUpdatableProperties(InternalOrderItem expectedInternalOrderItem) {
        assertInternalOrderItemAllUpdatablePropertiesEquals(
            expectedInternalOrderItem,
            getPersistedInternalOrderItem(expectedInternalOrderItem)
        );
    }
}
