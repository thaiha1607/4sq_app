package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.InternalOrderAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.InternalOrder;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.domain.enumeration.OrderType;
import com.foursquare.server.repository.InternalOrderRepository;
import com.foursquare.server.service.InternalOrderService;
import com.foursquare.server.service.dto.InternalOrderDTO;
import com.foursquare.server.service.mapper.InternalOrderMapper;
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
 * Integration tests for the {@link InternalOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InternalOrderResourceIT {

    private static final OrderType DEFAULT_TYPE = OrderType.SALE;
    private static final OrderType UPDATED_TYPE = OrderType.RETURN;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/internal-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InternalOrderRepository internalOrderRepository;

    @Mock
    private InternalOrderRepository internalOrderRepositoryMock;

    @Autowired
    private InternalOrderMapper internalOrderMapper;

    @Mock
    private InternalOrderService internalOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInternalOrderMockMvc;

    private InternalOrder internalOrder;

    private InternalOrder insertedInternalOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalOrder createEntity(EntityManager em) {
        InternalOrder internalOrder = new InternalOrder().type(DEFAULT_TYPE).note(DEFAULT_NOTE);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        internalOrder.setStatus(orderStatus);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        internalOrder.setRootOrder(order);
        return internalOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InternalOrder createUpdatedEntity(EntityManager em) {
        InternalOrder internalOrder = new InternalOrder().type(UPDATED_TYPE).note(UPDATED_NOTE);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createUpdatedEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        internalOrder.setStatus(orderStatus);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        internalOrder.setRootOrder(order);
        return internalOrder;
    }

    @BeforeEach
    public void initTest() {
        internalOrder = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInternalOrder != null) {
            internalOrderRepository.delete(insertedInternalOrder);
            insertedInternalOrder = null;
        }
    }

    @Test
    @Transactional
    void createInternalOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);
        var returnedInternalOrderDTO = om.readValue(
            restInternalOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InternalOrderDTO.class
        );

        // Validate the InternalOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInternalOrder = internalOrderMapper.toEntity(returnedInternalOrderDTO);
        assertInternalOrderUpdatableFieldsEquals(returnedInternalOrder, getPersistedInternalOrder(returnedInternalOrder));

        insertedInternalOrder = returnedInternalOrder;
    }

    @Test
    @Transactional
    void createInternalOrderWithExistingId() throws Exception {
        // Create the InternalOrder with an existing ID
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInternalOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        internalOrder.setType(null);

        // Create the InternalOrder, which fails.
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        restInternalOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInternalOrders() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList
        restInternalOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalOrder.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInternalOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(internalOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInternalOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(internalOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInternalOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(internalOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInternalOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(internalOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInternalOrder() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get the internalOrder
        restInternalOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, internalOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(internalOrder.getId().toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getInternalOrdersByIdFiltering() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        UUID id = internalOrder.getId();

        defaultInternalOrderFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where type equals to
        defaultInternalOrderFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where type in
        defaultInternalOrderFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where type is not null
        defaultInternalOrderFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllInternalOrdersByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where note equals to
        defaultInternalOrderFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where note in
        defaultInternalOrderFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where note is not null
        defaultInternalOrderFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllInternalOrdersByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where note contains
        defaultInternalOrderFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        // Get all the internalOrderList where note does not contain
        defaultInternalOrderFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllInternalOrdersByStatusIsEqualToSomething() throws Exception {
        OrderStatus status;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            internalOrderRepository.saveAndFlush(internalOrder);
            status = OrderStatusResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        em.persist(status);
        em.flush();
        internalOrder.setStatus(status);
        internalOrderRepository.saveAndFlush(internalOrder);
        Long statusId = status.getId();
        // Get all the internalOrderList where status equals to statusId
        defaultInternalOrderShouldBeFound("statusId.equals=" + statusId);

        // Get all the internalOrderList where status equals to (statusId + 1)
        defaultInternalOrderShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllInternalOrdersByRootOrderIsEqualToSomething() throws Exception {
        Order rootOrder;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            internalOrderRepository.saveAndFlush(internalOrder);
            rootOrder = OrderResourceIT.createEntity(em);
        } else {
            rootOrder = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(rootOrder);
        em.flush();
        internalOrder.setRootOrder(rootOrder);
        internalOrderRepository.saveAndFlush(internalOrder);
        UUID rootOrderId = rootOrder.getId();
        // Get all the internalOrderList where rootOrder equals to rootOrderId
        defaultInternalOrderShouldBeFound("rootOrderId.equals=" + rootOrderId);

        // Get all the internalOrderList where rootOrder equals to UUID.randomUUID()
        defaultInternalOrderShouldNotBeFound("rootOrderId.equals=" + UUID.randomUUID());
    }

    private void defaultInternalOrderFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInternalOrderShouldBeFound(shouldBeFound);
        defaultInternalOrderShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInternalOrderShouldBeFound(String filter) throws Exception {
        restInternalOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(internalOrder.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restInternalOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInternalOrderShouldNotBeFound(String filter) throws Exception {
        restInternalOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInternalOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInternalOrder() throws Exception {
        // Get the internalOrder
        restInternalOrderMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInternalOrder() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrder
        InternalOrder updatedInternalOrder = internalOrderRepository.findById(internalOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInternalOrder are not directly saved in db
        em.detach(updatedInternalOrder);
        updatedInternalOrder.type(UPDATED_TYPE).note(UPDATED_NOTE);
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(updatedInternalOrder);

        restInternalOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderDTO))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInternalOrderToMatchAllProperties(updatedInternalOrder);
    }

    @Test
    @Transactional
    void putNonExistingInternalOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrder.setId(UUID.randomUUID());

        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, internalOrderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInternalOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrder.setId(UUID.randomUUID());

        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(internalOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInternalOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrder.setId(UUID.randomUUID());

        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(internalOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInternalOrderWithPatch() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrder using partial update
        InternalOrder partialUpdatedInternalOrder = new InternalOrder();
        partialUpdatedInternalOrder.setId(internalOrder.getId());

        partialUpdatedInternalOrder.note(UPDATED_NOTE);

        restInternalOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInternalOrder))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInternalOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInternalOrder, internalOrder),
            getPersistedInternalOrder(internalOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateInternalOrderWithPatch() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the internalOrder using partial update
        InternalOrder partialUpdatedInternalOrder = new InternalOrder();
        partialUpdatedInternalOrder.setId(internalOrder.getId());

        partialUpdatedInternalOrder.type(UPDATED_TYPE).note(UPDATED_NOTE);

        restInternalOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInternalOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInternalOrder))
            )
            .andExpect(status().isOk());

        // Validate the InternalOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInternalOrderUpdatableFieldsEquals(partialUpdatedInternalOrder, getPersistedInternalOrder(partialUpdatedInternalOrder));
    }

    @Test
    @Transactional
    void patchNonExistingInternalOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrder.setId(UUID.randomUUID());

        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInternalOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, internalOrderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(internalOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInternalOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrder.setId(UUID.randomUUID());

        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(internalOrderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInternalOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        internalOrder.setId(UUID.randomUUID());

        // Create the InternalOrder
        InternalOrderDTO internalOrderDTO = internalOrderMapper.toDto(internalOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInternalOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(internalOrderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InternalOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInternalOrder() throws Exception {
        // Initialize the database
        insertedInternalOrder = internalOrderRepository.saveAndFlush(internalOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the internalOrder
        restInternalOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, internalOrder.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return internalOrderRepository.count();
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

    protected InternalOrder getPersistedInternalOrder(InternalOrder internalOrder) {
        return internalOrderRepository.findById(internalOrder.getId()).orElseThrow();
    }

    protected void assertPersistedInternalOrderToMatchAllProperties(InternalOrder expectedInternalOrder) {
        assertInternalOrderAllPropertiesEquals(expectedInternalOrder, getPersistedInternalOrder(expectedInternalOrder));
    }

    protected void assertPersistedInternalOrderToMatchUpdatableProperties(InternalOrder expectedInternalOrder) {
        assertInternalOrderAllUpdatablePropertiesEquals(expectedInternalOrder, getPersistedInternalOrder(expectedInternalOrder));
    }
}
