package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.OrderAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.enumeration.OrderType;
import com.foursquare.server.repository.OrderRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.repository.search.OrderSearchRepository;
import com.foursquare.server.service.OrderService;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.mapper.OrderMapper;
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
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final OrderType DEFAULT_TYPE = OrderType.SALE;
    private static final OrderType UPDATED_TYPE = OrderType.RETURN;

    private static final Integer DEFAULT_PRIORITY = 0;
    private static final Integer UPDATED_PRIORITY = 1;

    private static final Boolean DEFAULT_IS_INTERNAL = false;
    private static final Boolean UPDATED_IS_INTERNAL = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/orders/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Autowired
    private OrderMapper orderMapper;

    @Mock
    private OrderService orderServiceMock;

    @Autowired
    private OrderSearchRepository orderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    private Order insertedOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order().type(DEFAULT_TYPE).priority(DEFAULT_PRIORITY).isInternal(DEFAULT_IS_INTERNAL).note(DEFAULT_NOTE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        order.setCreator(user);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        order.setStatus(orderStatus);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order().type(UPDATED_TYPE).priority(UPDATED_PRIORITY).isInternal(UPDATED_IS_INTERNAL).note(UPDATED_NOTE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        order.setCreator(user);
        // Add required entity
        OrderStatus orderStatus;
        if (TestUtil.findAll(em, OrderStatus.class).isEmpty()) {
            orderStatus = OrderStatusResourceIT.createUpdatedEntity(em);
            em.persist(orderStatus);
            em.flush();
        } else {
            orderStatus = TestUtil.findAll(em, OrderStatus.class).get(0);
        }
        order.setStatus(orderStatus);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrder != null) {
            orderRepository.delete(insertedOrder);
            orderSearchRepository.delete(insertedOrder);
            insertedOrder = null;
        }
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        var returnedOrderDTO = om.readValue(
            restOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderDTO.class
        );

        // Validate the Order in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrder = orderMapper.toEntity(returnedOrderDTO);
        assertOrderUpdatableFieldsEquals(returnedOrder, getPersistedOrder(returnedOrder));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedOrder = returnedOrder;
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        insertedOrder = orderRepository.saveAndFlush(order);
        OrderDTO orderDTO = orderMapper.toDto(order);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        // set the field null
        order.setType(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].isInternal").value(hasItem(DEFAULT_IS_INTERNAL.booleanValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY))
            .andExpect(jsonPath("$.isInternal").value(DEFAULT_IS_INTERNAL.booleanValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrder() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderSearchRepository.save(order);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder.type(UPDATED_TYPE).priority(UPDATED_PRIORITY).isInternal(UPDATED_IS_INTERNAL).note(UPDATED_NOTE);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderToMatchAllProperties(updatedOrder);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Order> orderSearchList = Streamable.of(orderSearchRepository.findAll()).toList();
                Order testOrderSearch = orderSearchList.get(searchDatabaseSizeAfter - 1);

                assertOrderAllPropertiesEquals(testOrderSearch, updatedOrder);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrder, order), getPersistedOrder(order));
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder.type(UPDATED_TYPE).priority(UPDATED_PRIORITY).isInternal(UPDATED_IS_INTERNAL).note(UPDATED_NOTE);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderUpdatableFieldsEquals(partialUpdatedOrder, getPersistedOrder(partialUpdatedOrder));
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);
        orderRepository.save(order);
        orderSearchRepository.save(order);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrder() throws Exception {
        // Initialize the database
        insertedOrder = orderRepository.saveAndFlush(order);
        orderSearchRepository.save(order);

        // Search the order
        restOrderMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
            .andExpect(jsonPath("$.[*].isInternal").value(hasItem(DEFAULT_IS_INTERNAL.booleanValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    protected long getRepositoryCount() {
        return orderRepository.count();
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

    protected Order getPersistedOrder(Order order) {
        return orderRepository.findById(order.getId()).orElseThrow();
    }

    protected void assertPersistedOrderToMatchAllProperties(Order expectedOrder) {
        assertOrderAllPropertiesEquals(expectedOrder, getPersistedOrder(expectedOrder));
    }

    protected void assertPersistedOrderToMatchUpdatableProperties(Order expectedOrder) {
        assertOrderAllUpdatablePropertiesEquals(expectedOrder, getPersistedOrder(expectedOrder));
    }
}
