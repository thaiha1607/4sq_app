package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ShipmentItemAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static com.foursquare.server.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.repository.ShipmentItemRepository;
import com.foursquare.server.service.dto.ShipmentItemDTO;
import com.foursquare.server.service.mapper.ShipmentItemMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShipmentItemResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@AutoConfigureMockMvc
@WithMockUser
class ShipmentItemResourceIT {

    private static final Integer DEFAULT_QTY = 0;
    private static final Integer UPDATED_QTY = 1;
    private static final Integer SMALLER_QTY = 0 - 1;

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_ROLL_QTY = 0;
    private static final Integer UPDATED_ROLL_QTY = 1;
    private static final Integer SMALLER_ROLL_QTY = 0 - 1;

    private static final String ENTITY_API_URL = "/api/shipment-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentItemRepository shipmentItemRepository;

    @Autowired
    private ShipmentItemMapper shipmentItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentItemMockMvc;

    private ShipmentItem shipmentItem;

    private ShipmentItem insertedShipmentItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentItem createEntity(EntityManager em) {
        ShipmentItem shipmentItem = new ShipmentItem().qty(DEFAULT_QTY).total(DEFAULT_TOTAL).rollQty(DEFAULT_ROLL_QTY);
        return shipmentItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentItem createUpdatedEntity(EntityManager em) {
        ShipmentItem shipmentItem = new ShipmentItem().qty(UPDATED_QTY).total(UPDATED_TOTAL).rollQty(UPDATED_ROLL_QTY);
        return shipmentItem;
    }

    @BeforeEach
    public void initTest() {
        shipmentItem = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedShipmentItem != null) {
            shipmentItemRepository.delete(insertedShipmentItem);
            insertedShipmentItem = null;
        }
    }

    @Test
    @Transactional
    void createShipmentItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);
        var returnedShipmentItemDTO = om.readValue(
            restShipmentItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentItemDTO.class
        );

        // Validate the ShipmentItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipmentItem = shipmentItemMapper.toEntity(returnedShipmentItemDTO);
        assertShipmentItemUpdatableFieldsEquals(returnedShipmentItem, getPersistedShipmentItem(returnedShipmentItem));

        insertedShipmentItem = returnedShipmentItem;
    }

    @Test
    @Transactional
    void createShipmentItemWithExistingId() throws Exception {
        // Create the ShipmentItem with an existing ID
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipmentItem.setQty(null);

        // Create the ShipmentItem, which fails.
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipmentItem.setTotal(null);

        // Create the ShipmentItem, which fails.
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRollQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipmentItem.setRollQty(null);

        // Create the ShipmentItem, which fails.
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShipmentItems() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList
        restShipmentItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentItem.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].rollQty").value(hasItem(DEFAULT_ROLL_QTY)));
    }

    @Test
    @Transactional
    void getShipmentItem() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get the shipmentItem
        restShipmentItemMockMvc
            .perform(get(ENTITY_API_URL_ID, shipmentItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipmentItem.getId().toString()))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.rollQty").value(DEFAULT_ROLL_QTY));
    }

    @Test
    @Transactional
    void getShipmentItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        UUID id = shipmentItem.getId();

        defaultShipmentItemFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty equals to
        defaultShipmentItemFiltering("qty.equals=" + DEFAULT_QTY, "qty.equals=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty in
        defaultShipmentItemFiltering("qty.in=" + DEFAULT_QTY + "," + UPDATED_QTY, "qty.in=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty is not null
        defaultShipmentItemFiltering("qty.specified=true", "qty.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty is greater than or equal to
        defaultShipmentItemFiltering("qty.greaterThanOrEqual=" + DEFAULT_QTY, "qty.greaterThanOrEqual=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty is less than or equal to
        defaultShipmentItemFiltering("qty.lessThanOrEqual=" + DEFAULT_QTY, "qty.lessThanOrEqual=" + SMALLER_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty is less than
        defaultShipmentItemFiltering("qty.lessThan=" + UPDATED_QTY, "qty.lessThan=" + DEFAULT_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where qty is greater than
        defaultShipmentItemFiltering("qty.greaterThan=" + SMALLER_QTY, "qty.greaterThan=" + DEFAULT_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total equals to
        defaultShipmentItemFiltering("total.equals=" + DEFAULT_TOTAL, "total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total in
        defaultShipmentItemFiltering("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL, "total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total is not null
        defaultShipmentItemFiltering("total.specified=true", "total.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total is greater than or equal to
        defaultShipmentItemFiltering("total.greaterThanOrEqual=" + DEFAULT_TOTAL, "total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total is less than or equal to
        defaultShipmentItemFiltering("total.lessThanOrEqual=" + DEFAULT_TOTAL, "total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total is less than
        defaultShipmentItemFiltering("total.lessThan=" + UPDATED_TOTAL, "total.lessThan=" + DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where total is greater than
        defaultShipmentItemFiltering("total.greaterThan=" + SMALLER_TOTAL, "total.greaterThan=" + DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty equals to
        defaultShipmentItemFiltering("rollQty.equals=" + DEFAULT_ROLL_QTY, "rollQty.equals=" + UPDATED_ROLL_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty in
        defaultShipmentItemFiltering("rollQty.in=" + DEFAULT_ROLL_QTY + "," + UPDATED_ROLL_QTY, "rollQty.in=" + UPDATED_ROLL_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty is not null
        defaultShipmentItemFiltering("rollQty.specified=true", "rollQty.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty is greater than or equal to
        defaultShipmentItemFiltering("rollQty.greaterThanOrEqual=" + DEFAULT_ROLL_QTY, "rollQty.greaterThanOrEqual=" + UPDATED_ROLL_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty is less than or equal to
        defaultShipmentItemFiltering("rollQty.lessThanOrEqual=" + DEFAULT_ROLL_QTY, "rollQty.lessThanOrEqual=" + SMALLER_ROLL_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty is less than
        defaultShipmentItemFiltering("rollQty.lessThan=" + UPDATED_ROLL_QTY, "rollQty.lessThan=" + DEFAULT_ROLL_QTY);
    }

    @Test
    @Transactional
    void getAllShipmentItemsByRollQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        // Get all the shipmentItemList where rollQty is greater than
        defaultShipmentItemFiltering("rollQty.greaterThan=" + SMALLER_ROLL_QTY, "rollQty.greaterThan=" + DEFAULT_ROLL_QTY);
    }

    private void defaultShipmentItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShipmentItemShouldBeFound(shouldBeFound);
        defaultShipmentItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentItemShouldBeFound(String filter) throws Exception {
        restShipmentItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentItem.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].rollQty").value(hasItem(DEFAULT_ROLL_QTY)));

        // Check, that the count call also returns 1
        restShipmentItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentItemShouldNotBeFound(String filter) throws Exception {
        restShipmentItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShipmentItem() throws Exception {
        // Get the shipmentItem
        restShipmentItemMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipmentItem() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentItem
        ShipmentItem updatedShipmentItem = shipmentItemRepository.findById(shipmentItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentItem are not directly saved in db
        em.detach(updatedShipmentItem);
        updatedShipmentItem.qty(UPDATED_QTY).total(UPDATED_TOTAL).rollQty(UPDATED_ROLL_QTY);
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(updatedShipmentItem);

        restShipmentItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentItemToMatchAllProperties(updatedShipmentItem);
    }

    @Test
    @Transactional
    void putNonExistingShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShipmentItemWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentItem using partial update
        ShipmentItem partialUpdatedShipmentItem = new ShipmentItem();
        partialUpdatedShipmentItem.setId(shipmentItem.getId());

        partialUpdatedShipmentItem.rollQty(UPDATED_ROLL_QTY);

        restShipmentItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentItem))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShipmentItem, shipmentItem),
            getPersistedShipmentItem(shipmentItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateShipmentItemWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentItem using partial update
        ShipmentItem partialUpdatedShipmentItem = new ShipmentItem();
        partialUpdatedShipmentItem.setId(shipmentItem.getId());

        partialUpdatedShipmentItem.qty(UPDATED_QTY).total(UPDATED_TOTAL).rollQty(UPDATED_ROLL_QTY);

        restShipmentItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentItem))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentItemUpdatableFieldsEquals(partialUpdatedShipmentItem, getPersistedShipmentItem(partialUpdatedShipmentItem));
    }

    @Test
    @Transactional
    void patchNonExistingShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipmentItem() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipmentItem
        restShipmentItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentItem.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentItemRepository.count();
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

    protected ShipmentItem getPersistedShipmentItem(ShipmentItem shipmentItem) {
        return shipmentItemRepository.findById(shipmentItem.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentItemToMatchAllProperties(ShipmentItem expectedShipmentItem) {
        assertShipmentItemAllPropertiesEquals(expectedShipmentItem, getPersistedShipmentItem(expectedShipmentItem));
    }

    protected void assertPersistedShipmentItemToMatchUpdatableProperties(ShipmentItem expectedShipmentItem) {
        assertShipmentItemAllUpdatablePropertiesEquals(expectedShipmentItem, getPersistedShipmentItem(expectedShipmentItem));
    }
}
