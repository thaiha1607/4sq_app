package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.OrderStatusAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.repository.OrderStatusRepository;
import com.foursquare.server.service.dto.OrderStatusDTO;
import com.foursquare.server.service.mapper.OrderStatusMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link OrderStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderStatusResourceIT {

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderStatusMockMvc;

    private OrderStatus orderStatus;

    private OrderStatus insertedOrderStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderStatus createEntity(EntityManager em) {
        OrderStatus orderStatus = new OrderStatus().statusCode(DEFAULT_STATUS_CODE).description(DEFAULT_DESCRIPTION);
        return orderStatus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderStatus createUpdatedEntity(EntityManager em) {
        OrderStatus orderStatus = new OrderStatus().statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);
        return orderStatus;
    }

    @BeforeEach
    public void initTest() {
        orderStatus = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrderStatus != null) {
            orderStatusRepository.delete(insertedOrderStatus);
            insertedOrderStatus = null;
        }
    }

    @Test
    @Transactional
    void createOrderStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);
        var returnedOrderStatusDTO = om.readValue(
            restOrderStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderStatusDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderStatusDTO.class
        );

        // Validate the OrderStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderStatus = orderStatusMapper.toEntity(returnedOrderStatusDTO);
        assertOrderStatusUpdatableFieldsEquals(returnedOrderStatus, getPersistedOrderStatus(returnedOrderStatus));

        insertedOrderStatus = returnedOrderStatus;
    }

    @Test
    @Transactional
    void createOrderStatusWithExistingId() throws Exception {
        // Create the OrderStatus with an existing ID
        orderStatus.setId(1L);
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderStatus.setStatusCode(null);

        // Create the OrderStatus, which fails.
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        restOrderStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderStatusDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderStatuses() throws Exception {
        // Initialize the database
        insertedOrderStatus = orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList
        restOrderStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getOrderStatus() throws Exception {
        // Initialize the database
        insertedOrderStatus = orderStatusRepository.saveAndFlush(orderStatus);

        // Get the orderStatus
        restOrderStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, orderStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderStatus.getId().intValue()))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingOrderStatus() throws Exception {
        // Get the orderStatus
        restOrderStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderStatus() throws Exception {
        // Initialize the database
        insertedOrderStatus = orderStatusRepository.saveAndFlush(orderStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderStatus
        OrderStatus updatedOrderStatus = orderStatusRepository.findById(orderStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderStatus are not directly saved in db
        em.detach(updatedOrderStatus);
        updatedOrderStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(updatedOrderStatus);

        restOrderStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderStatusToMatchAllProperties(updatedOrderStatus);
    }

    @Test
    @Transactional
    void putNonExistingOrderStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatus.setId(longCount.incrementAndGet());

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderStatusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatus.setId(longCount.incrementAndGet());

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatus.setId(longCount.incrementAndGet());

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderStatusWithPatch() throws Exception {
        // Initialize the database
        insertedOrderStatus = orderStatusRepository.saveAndFlush(orderStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderStatus using partial update
        OrderStatus partialUpdatedOrderStatus = new OrderStatus();
        partialUpdatedOrderStatus.setId(orderStatus.getId());

        restOrderStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderStatus))
            )
            .andExpect(status().isOk());

        // Validate the OrderStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderStatus, orderStatus),
            getPersistedOrderStatus(orderStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderStatusWithPatch() throws Exception {
        // Initialize the database
        insertedOrderStatus = orderStatusRepository.saveAndFlush(orderStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderStatus using partial update
        OrderStatus partialUpdatedOrderStatus = new OrderStatus();
        partialUpdatedOrderStatus.setId(orderStatus.getId());

        partialUpdatedOrderStatus.statusCode(UPDATED_STATUS_CODE).description(UPDATED_DESCRIPTION);

        restOrderStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderStatus))
            )
            .andExpect(status().isOk());

        // Validate the OrderStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderStatusUpdatableFieldsEquals(partialUpdatedOrderStatus, getPersistedOrderStatus(partialUpdatedOrderStatus));
    }

    @Test
    @Transactional
    void patchNonExistingOrderStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatus.setId(longCount.incrementAndGet());

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderStatusDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatus.setId(longCount.incrementAndGet());

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatus.setId(longCount.incrementAndGet());

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderStatus() throws Exception {
        // Initialize the database
        insertedOrderStatus = orderStatusRepository.saveAndFlush(orderStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderStatus
        restOrderStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderStatus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderStatusRepository.count();
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

    protected OrderStatus getPersistedOrderStatus(OrderStatus orderStatus) {
        return orderStatusRepository.findById(orderStatus.getId()).orElseThrow();
    }

    protected void assertPersistedOrderStatusToMatchAllProperties(OrderStatus expectedOrderStatus) {
        assertOrderStatusAllPropertiesEquals(expectedOrderStatus, getPersistedOrderStatus(expectedOrderStatus));
    }

    protected void assertPersistedOrderStatusToMatchUpdatableProperties(OrderStatus expectedOrderStatus) {
        assertOrderStatusAllUpdatablePropertiesEquals(expectedOrderStatus, getPersistedOrderStatus(expectedOrderStatus));
    }
}
