package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ProductQuantityAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ProductQuantity;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.repository.ProductQuantityRepository;
import com.foursquare.server.repository.search.ProductQuantitySearchRepository;
import com.foursquare.server.service.ProductQuantityService;
import com.foursquare.server.service.dto.ProductQuantityDTO;
import com.foursquare.server.service.mapper.ProductQuantityMapper;
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
 * Integration tests for the {@link ProductQuantityResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductQuantityResourceIT {

    private static final Integer DEFAULT_QTY = 0;
    private static final Integer UPDATED_QTY = 1;

    private static final String ENTITY_API_URL = "/api/product-quantities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/product-quantities/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductQuantityRepository productQuantityRepository;

    @Mock
    private ProductQuantityRepository productQuantityRepositoryMock;

    @Autowired
    private ProductQuantityMapper productQuantityMapper;

    @Mock
    private ProductQuantityService productQuantityServiceMock;

    @Autowired
    private ProductQuantitySearchRepository productQuantitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductQuantityMockMvc;

    private ProductQuantity productQuantity;

    private ProductQuantity insertedProductQuantity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductQuantity createEntity(EntityManager em) {
        ProductQuantity productQuantity = new ProductQuantity().qty(DEFAULT_QTY);
        // Add required entity
        WorkingUnit workingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            workingUnit = WorkingUnitResourceIT.createEntity(em);
            em.persist(workingUnit);
            em.flush();
        } else {
            workingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        productQuantity.setWorkingUnit(workingUnit);
        return productQuantity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductQuantity createUpdatedEntity(EntityManager em) {
        ProductQuantity productQuantity = new ProductQuantity().qty(UPDATED_QTY);
        // Add required entity
        WorkingUnit workingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            workingUnit = WorkingUnitResourceIT.createUpdatedEntity(em);
            em.persist(workingUnit);
            em.flush();
        } else {
            workingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        productQuantity.setWorkingUnit(workingUnit);
        return productQuantity;
    }

    @BeforeEach
    public void initTest() {
        productQuantity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductQuantity != null) {
            productQuantityRepository.delete(insertedProductQuantity);
            productQuantitySearchRepository.delete(insertedProductQuantity);
            insertedProductQuantity = null;
        }
    }

    @Test
    @Transactional
    void createProductQuantity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);
        var returnedProductQuantityDTO = om.readValue(
            restProductQuantityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductQuantityDTO.class
        );

        // Validate the ProductQuantity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductQuantity = productQuantityMapper.toEntity(returnedProductQuantityDTO);
        assertProductQuantityUpdatableFieldsEquals(returnedProductQuantity, getPersistedProductQuantity(returnedProductQuantity));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProductQuantity = returnedProductQuantity;
    }

    @Test
    @Transactional
    void createProductQuantityWithExistingId() throws Exception {
        // Create the ProductQuantity with an existing ID
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductQuantityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        // set the field null
        productQuantity.setQty(null);

        // Create the ProductQuantity, which fails.
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        restProductQuantityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProductQuantities() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList
        restProductQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productQuantity.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductQuantitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(productQuantityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductQuantityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productQuantityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductQuantitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productQuantityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductQuantityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productQuantityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductQuantity() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get the productQuantity
        restProductQuantityMockMvc
            .perform(get(ENTITY_API_URL_ID, productQuantity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productQuantity.getId().toString()))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY));
    }

    @Test
    @Transactional
    void getNonExistingProductQuantity() throws Exception {
        // Get the productQuantity
        restProductQuantityMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductQuantity() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuantitySearchRepository.save(productQuantity);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());

        // Update the productQuantity
        ProductQuantity updatedProductQuantity = productQuantityRepository.findById(productQuantity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductQuantity are not directly saved in db
        em.detach(updatedProductQuantity);
        updatedProductQuantity.qty(UPDATED_QTY);
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(updatedProductQuantity);

        restProductQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productQuantityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productQuantityDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductQuantityToMatchAllProperties(updatedProductQuantity);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductQuantity> productQuantitySearchList = Streamable.of(productQuantitySearchRepository.findAll()).toList();
                ProductQuantity testProductQuantitySearch = productQuantitySearchList.get(searchDatabaseSizeAfter - 1);

                assertProductQuantityAllPropertiesEquals(testProductQuantitySearch, updatedProductQuantity);
            });
    }

    @Test
    @Transactional
    void putNonExistingProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productQuantityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productQuantityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productQuantityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProductQuantityWithPatch() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productQuantity using partial update
        ProductQuantity partialUpdatedProductQuantity = new ProductQuantity();
        partialUpdatedProductQuantity.setId(productQuantity.getId());

        partialUpdatedProductQuantity.qty(UPDATED_QTY);

        restProductQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductQuantity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductQuantity))
            )
            .andExpect(status().isOk());

        // Validate the ProductQuantity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductQuantityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductQuantity, productQuantity),
            getPersistedProductQuantity(productQuantity)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductQuantityWithPatch() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productQuantity using partial update
        ProductQuantity partialUpdatedProductQuantity = new ProductQuantity();
        partialUpdatedProductQuantity.setId(productQuantity.getId());

        partialUpdatedProductQuantity.qty(UPDATED_QTY);

        restProductQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductQuantity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductQuantity))
            )
            .andExpect(status().isOk());

        // Validate the ProductQuantity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductQuantityUpdatableFieldsEquals(
            partialUpdatedProductQuantity,
            getPersistedProductQuantity(partialUpdatedProductQuantity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productQuantityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productQuantityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productQuantityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProductQuantity() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);
        productQuantityRepository.save(productQuantity);
        productQuantitySearchRepository.save(productQuantity);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productQuantity
        restProductQuantityMockMvc
            .perform(delete(ENTITY_API_URL_ID, productQuantity.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productQuantitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProductQuantity() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);
        productQuantitySearchRepository.save(productQuantity);

        // Search the productQuantity
        restProductQuantityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + productQuantity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productQuantity.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)));
    }

    protected long getRepositoryCount() {
        return productQuantityRepository.count();
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

    protected ProductQuantity getPersistedProductQuantity(ProductQuantity productQuantity) {
        return productQuantityRepository.findById(productQuantity.getId()).orElseThrow();
    }

    protected void assertPersistedProductQuantityToMatchAllProperties(ProductQuantity expectedProductQuantity) {
        assertProductQuantityAllPropertiesEquals(expectedProductQuantity, getPersistedProductQuantity(expectedProductQuantity));
    }

    protected void assertPersistedProductQuantityToMatchUpdatableProperties(ProductQuantity expectedProductQuantity) {
        assertProductQuantityAllUpdatablePropertiesEquals(expectedProductQuantity, getPersistedProductQuantity(expectedProductQuantity));
    }
}
