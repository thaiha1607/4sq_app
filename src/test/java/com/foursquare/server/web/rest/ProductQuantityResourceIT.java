package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ProductQuantityAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ProductQuantity;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.repository.ProductQuantityRepository;
import com.foursquare.server.service.ProductQuantityService;
import com.foursquare.server.service.dto.ProductQuantityDTO;
import com.foursquare.server.service.mapper.ProductQuantityMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.UUID;
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
    private static final Integer SMALLER_QTY = 0 - 1;

    private static final String ENTITY_API_URL = "/api/product-quantities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

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
            insertedProductQuantity = null;
        }
    }

    @Test
    @Transactional
    void createProductQuantity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
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

        insertedProductQuantity = returnedProductQuantity;
    }

    @Test
    @Transactional
    void createProductQuantityWithExistingId() throws Exception {
        // Create the ProductQuantity with an existing ID
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductQuantityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productQuantity.setQty(null);

        // Create the ProductQuantity, which fails.
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        restProductQuantityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
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
    void getProductQuantitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        UUID id = productQuantity.getId();

        defaultProductQuantityFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty equals to
        defaultProductQuantityFiltering("qty.equals=" + DEFAULT_QTY, "qty.equals=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty in
        defaultProductQuantityFiltering("qty.in=" + DEFAULT_QTY + "," + UPDATED_QTY, "qty.in=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty is not null
        defaultProductQuantityFiltering("qty.specified=true", "qty.specified=false");
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty is greater than or equal to
        defaultProductQuantityFiltering("qty.greaterThanOrEqual=" + DEFAULT_QTY, "qty.greaterThanOrEqual=" + UPDATED_QTY);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty is less than or equal to
        defaultProductQuantityFiltering("qty.lessThanOrEqual=" + DEFAULT_QTY, "qty.lessThanOrEqual=" + SMALLER_QTY);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty is less than
        defaultProductQuantityFiltering("qty.lessThan=" + UPDATED_QTY, "qty.lessThan=" + DEFAULT_QTY);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        // Get all the productQuantityList where qty is greater than
        defaultProductQuantityFiltering("qty.greaterThan=" + SMALLER_QTY, "qty.greaterThan=" + DEFAULT_QTY);
    }

    @Test
    @Transactional
    void getAllProductQuantitiesByWorkingUnitIsEqualToSomething() throws Exception {
        WorkingUnit workingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            productQuantityRepository.saveAndFlush(productQuantity);
            workingUnit = WorkingUnitResourceIT.createEntity(em);
        } else {
            workingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        em.persist(workingUnit);
        em.flush();
        productQuantity.setWorkingUnit(workingUnit);
        productQuantityRepository.saveAndFlush(productQuantity);
        UUID workingUnitId = workingUnit.getId();
        // Get all the productQuantityList where workingUnit equals to workingUnitId
        defaultProductQuantityShouldBeFound("workingUnitId.equals=" + workingUnitId);

        // Get all the productQuantityList where workingUnit equals to UUID.randomUUID()
        defaultProductQuantityShouldNotBeFound("workingUnitId.equals=" + UUID.randomUUID());
    }

    private void defaultProductQuantityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProductQuantityShouldBeFound(shouldBeFound);
        defaultProductQuantityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductQuantityShouldBeFound(String filter) throws Exception {
        restProductQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productQuantity.getId().toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)));

        // Check, that the count call also returns 1
        restProductQuantityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductQuantityShouldNotBeFound(String filter) throws Exception {
        restProductQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductQuantityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
    }

    @Test
    @Transactional
    void putNonExistingProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void putWithIdMismatchProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
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
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductQuantity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuantity.setId(UUID.randomUUID());

        // Create the ProductQuantity
        ProductQuantityDTO productQuantityDTO = productQuantityMapper.toDto(productQuantity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuantityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productQuantityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductQuantity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductQuantity() throws Exception {
        // Initialize the database
        insertedProductQuantity = productQuantityRepository.saveAndFlush(productQuantity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productQuantity
        restProductQuantityMockMvc
            .perform(delete(ENTITY_API_URL_ID, productQuantity.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
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
