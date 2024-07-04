package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ShipmentItemAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static com.foursquare.server.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.repository.ShipmentItemRepository;
import com.foursquare.server.repository.search.ShipmentItemSearchRepository;
import com.foursquare.server.service.dto.ShipmentItemDTO;
import com.foursquare.server.service.mapper.ShipmentItemMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
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

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/shipment-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/shipment-items/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentItemRepository shipmentItemRepository;

    @Autowired
    private ShipmentItemMapper shipmentItemMapper;

    @Autowired
    private ShipmentItemSearchRepository shipmentItemSearchRepository;

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
        ShipmentItem shipmentItem = new ShipmentItem().qty(DEFAULT_QTY).total(DEFAULT_TOTAL);
        return shipmentItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentItem createUpdatedEntity(EntityManager em) {
        ShipmentItem shipmentItem = new ShipmentItem().qty(UPDATED_QTY).total(UPDATED_TOTAL);
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
            shipmentItemSearchRepository.delete(insertedShipmentItem);
            insertedShipmentItem = null;
        }
    }

    @Test
    @Transactional
    void createShipmentItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
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

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedShipmentItem = returnedShipmentItem;
    }

    @Test
    @Transactional
    void createShipmentItemWithExistingId() throws Exception {
        // Create the ShipmentItem with an existing ID
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        // set the field null
        shipmentItem.setQty(null);

        // Create the ShipmentItem, which fails.
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        // set the field null
        shipmentItem.setTotal(null);

        // Create the ShipmentItem, which fails.
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        restShipmentItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
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
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
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
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)));
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
        shipmentItemSearchRepository.save(shipmentItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());

        // Update the shipmentItem
        ShipmentItem updatedShipmentItem = shipmentItemRepository.findById(shipmentItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentItem are not directly saved in db
        em.detach(updatedShipmentItem);
        updatedShipmentItem.qty(UPDATED_QTY).total(UPDATED_TOTAL);
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

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ShipmentItem> shipmentItemSearchList = Streamable.of(shipmentItemSearchRepository.findAll()).toList();
                ShipmentItem testShipmentItemSearch = shipmentItemSearchList.get(searchDatabaseSizeAfter - 1);

                assertShipmentItemAllPropertiesEquals(testShipmentItemSearch, updatedShipmentItem);
            });
    }

    @Test
    @Transactional
    void putNonExistingShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
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

        partialUpdatedShipmentItem.qty(UPDATED_QTY).total(UPDATED_TOTAL);

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
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        shipmentItem.setId(UUID.randomUUID());

        // Create the ShipmentItem
        ShipmentItemDTO shipmentItemDTO = shipmentItemMapper.toDto(shipmentItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteShipmentItem() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);
        shipmentItemRepository.save(shipmentItem);
        shipmentItemSearchRepository.save(shipmentItem);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the shipmentItem
        restShipmentItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentItem.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchShipmentItem() throws Exception {
        // Initialize the database
        insertedShipmentItem = shipmentItemRepository.saveAndFlush(shipmentItem);
        shipmentItemSearchRepository.save(shipmentItem);

        // Search the shipmentItem
        restShipmentItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + shipmentItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentItem.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
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
