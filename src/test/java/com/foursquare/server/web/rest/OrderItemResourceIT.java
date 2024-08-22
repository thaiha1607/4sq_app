package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.OrderItemAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static com.foursquare.server.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.domain.ProductCategory;
import com.foursquare.server.repository.OrderItemRepository;
import com.foursquare.server.service.OrderItemService;
import com.foursquare.server.service.dto.OrderItemDTO;
import com.foursquare.server.service.mapper.OrderItemMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link OrderItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderItemResourceIT {

    private static final Integer DEFAULT_ORDERED_QTY = 0;
    private static final Integer UPDATED_ORDERED_QTY = 1;
    private static final Integer SMALLER_ORDERED_QTY = 0 - 1;

    private static final Integer DEFAULT_RECEIVED_QTY = 0;
    private static final Integer UPDATED_RECEIVED_QTY = 1;
    private static final Integer SMALLER_RECEIVED_QTY = 0 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemRepository orderItemRepositoryMock;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderItemService orderItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemMockMvc;

    private OrderItem orderItem;

    private OrderItem insertedOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createEntity(EntityManager em) {
        OrderItem orderItem = new OrderItem()
            .orderedQty(DEFAULT_ORDERED_QTY)
            .receivedQty(DEFAULT_RECEIVED_QTY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .note(DEFAULT_NOTE);
        // Add required entity
        ProductCategory productCategory;
        if (TestUtil.findAll(em, ProductCategory.class).isEmpty()) {
            productCategory = ProductCategoryResourceIT.createEntity(em);
            em.persist(productCategory);
            em.flush();
        } else {
            productCategory = TestUtil.findAll(em, ProductCategory.class).get(0);
        }
        orderItem.setProductCategory(productCategory);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        orderItem.setOrder(order);
        return orderItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createUpdatedEntity(EntityManager em) {
        OrderItem orderItem = new OrderItem()
            .orderedQty(UPDATED_ORDERED_QTY)
            .receivedQty(UPDATED_RECEIVED_QTY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .note(UPDATED_NOTE);
        // Add required entity
        ProductCategory productCategory;
        if (TestUtil.findAll(em, ProductCategory.class).isEmpty()) {
            productCategory = ProductCategoryResourceIT.createUpdatedEntity(em);
            em.persist(productCategory);
            em.flush();
        } else {
            productCategory = TestUtil.findAll(em, ProductCategory.class).get(0);
        }
        orderItem.setProductCategory(productCategory);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        orderItem.setOrder(order);
        return orderItem;
    }

    @BeforeEach
    public void initTest() {
        orderItem = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrderItem != null) {
            orderItemRepository.delete(insertedOrderItem);
            insertedOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        var returnedOrderItemDTO = om.readValue(
            restOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderItemDTO.class
        );

        // Validate the OrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderItem = orderItemMapper.toEntity(returnedOrderItemDTO);
        assertOrderItemUpdatableFieldsEquals(returnedOrderItem, getPersistedOrderItem(returnedOrderItem));

        insertedOrderItem = returnedOrderItem;
    }

    @Test
    @Transactional
    void createOrderItemWithExistingId() throws Exception {
        // Create the OrderItem with an existing ID
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderedQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItem.setOrderedQty(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceivedQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItem.setReceivedQty(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItem.setUnitPrice(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        restOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderItems() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().toString())))
            .andExpect(jsonPath("$.[*].orderedQty").value(hasItem(DEFAULT_ORDERED_QTY)))
            .andExpect(jsonPath("$.[*].receivedQty").value(hasItem(DEFAULT_RECEIVED_QTY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get the orderItem
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, orderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItem.getId().toString()))
            .andExpect(jsonPath("$.orderedQty").value(DEFAULT_ORDERED_QTY))
            .andExpect(jsonPath("$.receivedQty").value(DEFAULT_RECEIVED_QTY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getOrderItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        UUID id = orderItem.getId();

        defaultOrderItemFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty equals to
        defaultOrderItemFiltering("orderedQty.equals=" + DEFAULT_ORDERED_QTY, "orderedQty.equals=" + UPDATED_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty in
        defaultOrderItemFiltering(
            "orderedQty.in=" + DEFAULT_ORDERED_QTY + "," + UPDATED_ORDERED_QTY,
            "orderedQty.in=" + UPDATED_ORDERED_QTY
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty is not null
        defaultOrderItemFiltering("orderedQty.specified=true", "orderedQty.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty is greater than or equal to
        defaultOrderItemFiltering(
            "orderedQty.greaterThanOrEqual=" + DEFAULT_ORDERED_QTY,
            "orderedQty.greaterThanOrEqual=" + UPDATED_ORDERED_QTY
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty is less than or equal to
        defaultOrderItemFiltering("orderedQty.lessThanOrEqual=" + DEFAULT_ORDERED_QTY, "orderedQty.lessThanOrEqual=" + SMALLER_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty is less than
        defaultOrderItemFiltering("orderedQty.lessThan=" + UPDATED_ORDERED_QTY, "orderedQty.lessThan=" + DEFAULT_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderedQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where orderedQty is greater than
        defaultOrderItemFiltering("orderedQty.greaterThan=" + SMALLER_ORDERED_QTY, "orderedQty.greaterThan=" + DEFAULT_ORDERED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty equals to
        defaultOrderItemFiltering("receivedQty.equals=" + DEFAULT_RECEIVED_QTY, "receivedQty.equals=" + UPDATED_RECEIVED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty in
        defaultOrderItemFiltering(
            "receivedQty.in=" + DEFAULT_RECEIVED_QTY + "," + UPDATED_RECEIVED_QTY,
            "receivedQty.in=" + UPDATED_RECEIVED_QTY
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty is not null
        defaultOrderItemFiltering("receivedQty.specified=true", "receivedQty.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty is greater than or equal to
        defaultOrderItemFiltering(
            "receivedQty.greaterThanOrEqual=" + DEFAULT_RECEIVED_QTY,
            "receivedQty.greaterThanOrEqual=" + UPDATED_RECEIVED_QTY
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty is less than or equal to
        defaultOrderItemFiltering(
            "receivedQty.lessThanOrEqual=" + DEFAULT_RECEIVED_QTY,
            "receivedQty.lessThanOrEqual=" + SMALLER_RECEIVED_QTY
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty is less than
        defaultOrderItemFiltering("receivedQty.lessThan=" + UPDATED_RECEIVED_QTY, "receivedQty.lessThan=" + DEFAULT_RECEIVED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByReceivedQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where receivedQty is greater than
        defaultOrderItemFiltering("receivedQty.greaterThan=" + SMALLER_RECEIVED_QTY, "receivedQty.greaterThan=" + DEFAULT_RECEIVED_QTY);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice equals to
        defaultOrderItemFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice in
        defaultOrderItemFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is not null
        defaultOrderItemFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is greater than or equal to
        defaultOrderItemFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is less than or equal to
        defaultOrderItemFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is less than
        defaultOrderItemFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where unitPrice is greater than
        defaultOrderItemFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where note equals to
        defaultOrderItemFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where note in
        defaultOrderItemFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where note is not null
        defaultOrderItemFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderItemsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where note contains
        defaultOrderItemFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        // Get all the orderItemList where note does not contain
        defaultOrderItemFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllOrderItemsByProductCategoryIsEqualToSomething() throws Exception {
        ProductCategory productCategory;
        if (TestUtil.findAll(em, ProductCategory.class).isEmpty()) {
            orderItemRepository.saveAndFlush(orderItem);
            productCategory = ProductCategoryResourceIT.createEntity(em);
        } else {
            productCategory = TestUtil.findAll(em, ProductCategory.class).get(0);
        }
        em.persist(productCategory);
        em.flush();
        orderItem.setProductCategory(productCategory);
        orderItemRepository.saveAndFlush(orderItem);
        UUID productCategoryId = productCategory.getId();
        // Get all the orderItemList where productCategory equals to productCategoryId
        defaultOrderItemShouldBeFound("productCategoryId.equals=" + productCategoryId);

        // Get all the orderItemList where productCategory equals to UUID.randomUUID()
        defaultOrderItemShouldNotBeFound("productCategoryId.equals=" + UUID.randomUUID());
    }

    @Test
    @Transactional
    void getAllOrderItemsByOrderIsEqualToSomething() throws Exception {
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            orderItemRepository.saveAndFlush(orderItem);
            order = OrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderItem.setOrder(order);
        orderItemRepository.saveAndFlush(orderItem);
        UUID orderId = order.getId();
        // Get all the orderItemList where order equals to orderId
        defaultOrderItemShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderItemList where order equals to UUID.randomUUID()
        defaultOrderItemShouldNotBeFound("orderId.equals=" + UUID.randomUUID());
    }

    private void defaultOrderItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrderItemShouldBeFound(shouldBeFound);
        defaultOrderItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderItemShouldBeFound(String filter) throws Exception {
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.getId().toString())))
            .andExpect(jsonPath("$.[*].orderedQty").value(hasItem(DEFAULT_ORDERED_QTY)))
            .andExpect(jsonPath("$.[*].receivedQty").value(hasItem(DEFAULT_RECEIVED_QTY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderItemShouldNotBeFound(String filter) throws Exception {
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderItem() throws Exception {
        // Get the orderItem
        restOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderItem are not directly saved in db
        em.detach(updatedOrderItem);
        updatedOrderItem.orderedQty(UPDATED_ORDERED_QTY).receivedQty(UPDATED_RECEIVED_QTY).unitPrice(UPDATED_UNIT_PRICE).note(UPDATED_NOTE);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(updatedOrderItem);

        restOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemToMatchAllProperties(updatedOrderItem);
    }

    @Test
    @Transactional
    void putNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem.orderedQty(UPDATED_ORDERED_QTY).receivedQty(UPDATED_RECEIVED_QTY).note(UPDATED_NOTE);

        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItem, orderItem),
            getPersistedOrderItem(orderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem
            .orderedQty(UPDATED_ORDERED_QTY)
            .receivedQty(UPDATED_RECEIVED_QTY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .note(UPDATED_NOTE);

        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(partialUpdatedOrderItem, getPersistedOrderItem(partialUpdatedOrderItem));
    }

    @Test
    @Transactional
    void patchNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.saveAndFlush(orderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderItem
        restOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderItem.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderItemRepository.count();
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

    protected OrderItem getPersistedOrderItem(OrderItem orderItem) {
        return orderItemRepository.findById(orderItem.getId()).orElseThrow();
    }

    protected void assertPersistedOrderItemToMatchAllProperties(OrderItem expectedOrderItem) {
        assertOrderItemAllPropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }

    protected void assertPersistedOrderItemToMatchUpdatableProperties(OrderItem expectedOrderItem) {
        assertOrderItemAllUpdatablePropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }
}
