package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.OrderHistoryAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderHistory;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.repository.OrderHistoryRepository;
import com.foursquare.server.service.OrderHistoryService;
import com.foursquare.server.service.dto.OrderHistoryDTO;
import com.foursquare.server.service.mapper.OrderHistoryMapper;
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
 * Integration tests for the {@link OrderHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderHistoryResourceIT {

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Mock
    private OrderHistoryRepository orderHistoryRepositoryMock;

    @Autowired
    private OrderHistoryMapper orderHistoryMapper;

    @Mock
    private OrderHistoryService orderHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderHistoryMockMvc;

    private OrderHistory orderHistory;

    private OrderHistory insertedOrderHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderHistory createEntity(EntityManager em) {
        OrderHistory orderHistory = new OrderHistory().comments(DEFAULT_COMMENTS);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        orderHistory.setStatus(orderStatus);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        orderHistory.setOrder(order);
        return orderHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderHistory createUpdatedEntity(EntityManager em) {
        OrderHistory orderHistory = new OrderHistory().comments(UPDATED_COMMENTS);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createUpdatedEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        orderHistory.setStatus(orderStatus);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        orderHistory.setOrder(order);
        return orderHistory;
    }

    @BeforeEach
    public void initTest() {
        orderHistory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrderHistory != null) {
            orderHistoryRepository.delete(insertedOrderHistory);
            insertedOrderHistory = null;
        }
    }

    @Test
    @Transactional
    void createOrderHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);
        var returnedOrderHistoryDTO = om.readValue(
            restOrderHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderHistoryDTO.class
        );

        // Validate the OrderHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderHistory = orderHistoryMapper.toEntity(returnedOrderHistoryDTO);
        assertOrderHistoryUpdatableFieldsEquals(returnedOrderHistory, getPersistedOrderHistory(returnedOrderHistory));

        insertedOrderHistory = returnedOrderHistory;
    }

    @Test
    @Transactional
    void createOrderHistoryWithExistingId() throws Exception {
        // Create the OrderHistory with an existing ID
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderHistories() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderHistory.getId().toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderHistoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrderHistory() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get the orderHistory
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, orderHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderHistory.getId().toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getOrderHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        UUID id = orderHistory.getId();

        defaultOrderHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList where comments equals to
        defaultOrderHistoryFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList where comments in
        defaultOrderHistoryFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList where comments is not null
        defaultOrderHistoryFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList where comments contains
        defaultOrderHistoryFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList where comments does not contain
        defaultOrderHistoryFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByStatusIsEqualToSomething() throws Exception {
        OrderStatus status;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderHistoryRepository.saveAndFlush(orderHistory);
            status = OrderStatusResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        em.persist(status);
        em.flush();
        orderHistory.setStatus(status);
        orderHistoryRepository.saveAndFlush(orderHistory);
        Long statusId = status.getId();
        // Get all the orderHistoryList where status equals to statusId
        defaultOrderHistoryShouldBeFound("statusId.equals=" + statusId);

        // Get all the orderHistoryList where status equals to (statusId + 1)
        defaultOrderHistoryShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllOrderHistoriesByOrderIsEqualToSomething() throws Exception {
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            orderHistoryRepository.saveAndFlush(orderHistory);
            order = OrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderHistory.setOrder(order);
        orderHistoryRepository.saveAndFlush(orderHistory);
        UUID orderId = order.getId();
        // Get all the orderHistoryList where order equals to orderId
        defaultOrderHistoryShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderHistoryList where order equals to UUID.randomUUID()
        defaultOrderHistoryShouldNotBeFound("orderId.equals=" + UUID.randomUUID());
    }

    private void defaultOrderHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrderHistoryShouldBeFound(shouldBeFound);
        defaultOrderHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderHistoryShouldBeFound(String filter) throws Exception {
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderHistory.getId().toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));

        // Check, that the count call also returns 1
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderHistoryShouldNotBeFound(String filter) throws Exception {
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderHistory() throws Exception {
        // Get the orderHistory
        restOrderHistoryMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderHistory() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderHistory
        OrderHistory updatedOrderHistory = orderHistoryRepository.findById(orderHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderHistory are not directly saved in db
        em.detach(updatedOrderHistory);
        updatedOrderHistory.comments(UPDATED_COMMENTS);
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(updatedOrderHistory);

        restOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderHistoryToMatchAllProperties(updatedOrderHistory);
    }

    @Test
    @Transactional
    void putNonExistingOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderHistory.setId(UUID.randomUUID());

        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderHistory.setId(UUID.randomUUID());

        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderHistory.setId(UUID.randomUUID());

        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderHistory using partial update
        OrderHistory partialUpdatedOrderHistory = new OrderHistory();
        partialUpdatedOrderHistory.setId(orderHistory.getId());

        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the OrderHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderHistory, orderHistory),
            getPersistedOrderHistory(orderHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderHistory using partial update
        OrderHistory partialUpdatedOrderHistory = new OrderHistory();
        partialUpdatedOrderHistory.setId(orderHistory.getId());

        partialUpdatedOrderHistory.comments(UPDATED_COMMENTS);

        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the OrderHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderHistoryUpdatableFieldsEquals(partialUpdatedOrderHistory, getPersistedOrderHistory(partialUpdatedOrderHistory));
    }

    @Test
    @Transactional
    void patchNonExistingOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderHistory.setId(UUID.randomUUID());

        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderHistory.setId(UUID.randomUUID());

        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderHistory.setId(UUID.randomUUID());

        // Create the OrderHistory
        OrderHistoryDTO orderHistoryDTO = orderHistoryMapper.toDto(orderHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderHistory() throws Exception {
        // Initialize the database
        insertedOrderHistory = orderHistoryRepository.saveAndFlush(orderHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderHistory
        restOrderHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderHistory.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderHistoryRepository.count();
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

    protected OrderHistory getPersistedOrderHistory(OrderHistory orderHistory) {
        return orderHistoryRepository.findById(orderHistory.getId()).orElseThrow();
    }

    protected void assertPersistedOrderHistoryToMatchAllProperties(OrderHistory expectedOrderHistory) {
        assertOrderHistoryAllPropertiesEquals(expectedOrderHistory, getPersistedOrderHistory(expectedOrderHistory));
    }

    protected void assertPersistedOrderHistoryToMatchUpdatableProperties(OrderHistory expectedOrderHistory) {
        assertOrderHistoryAllUpdatablePropertiesEquals(expectedOrderHistory, getPersistedOrderHistory(expectedOrderHistory));
    }
}
