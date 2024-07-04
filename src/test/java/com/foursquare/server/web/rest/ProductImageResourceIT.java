package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ProductImageAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ProductImage;
import com.foursquare.server.repository.ProductImageRepository;
import com.foursquare.server.repository.search.ProductImageSearchRepository;
import com.foursquare.server.service.dto.ProductImageDTO;
import com.foursquare.server.service.mapper.ProductImageMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ProductImageResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@AutoConfigureMockMvc
@WithMockUser
class ProductImageResourceIT {

    private static final String DEFAULT_IMAGE_URI = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URI = "BBBBBBBBBB";

    private static final String DEFAULT_ALT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ALT_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/product-images/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductImageSearchRepository productImageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductImageMockMvc;

    private ProductImage productImage;

    private ProductImage insertedProductImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductImage createEntity(EntityManager em) {
        ProductImage productImage = new ProductImage().imageUri(DEFAULT_IMAGE_URI).altText(DEFAULT_ALT_TEXT);
        return productImage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductImage createUpdatedEntity(EntityManager em) {
        ProductImage productImage = new ProductImage().imageUri(UPDATED_IMAGE_URI).altText(UPDATED_ALT_TEXT);
        return productImage;
    }

    @BeforeEach
    public void initTest() {
        productImage = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductImage != null) {
            productImageRepository.delete(insertedProductImage);
            productImageSearchRepository.delete(insertedProductImage);
            insertedProductImage = null;
        }
    }

    @Test
    @Transactional
    void createProductImage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);
        var returnedProductImageDTO = om.readValue(
            restProductImageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductImageDTO.class
        );

        // Validate the ProductImage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductImage = productImageMapper.toEntity(returnedProductImageDTO);
        assertProductImageUpdatableFieldsEquals(returnedProductImage, getPersistedProductImage(returnedProductImage));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProductImage = returnedProductImage;
    }

    @Test
    @Transactional
    void createProductImageWithExistingId() throws Exception {
        // Create the ProductImage with an existing ID
        insertedProductImage = productImageRepository.saveAndFlush(productImage);
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkImageUriIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        // set the field null
        productImage.setImageUri(null);

        // Create the ProductImage, which fails.
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        restProductImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProductImages() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        // Get all the productImageList
        restProductImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productImage.getId().toString())))
            .andExpect(jsonPath("$.[*].imageUri").value(hasItem(DEFAULT_IMAGE_URI)))
            .andExpect(jsonPath("$.[*].altText").value(hasItem(DEFAULT_ALT_TEXT)));
    }

    @Test
    @Transactional
    void getProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        // Get the productImage
        restProductImageMockMvc
            .perform(get(ENTITY_API_URL_ID, productImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productImage.getId().toString()))
            .andExpect(jsonPath("$.imageUri").value(DEFAULT_IMAGE_URI))
            .andExpect(jsonPath("$.altText").value(DEFAULT_ALT_TEXT));
    }

    @Test
    @Transactional
    void getNonExistingProductImage() throws Exception {
        // Get the productImage
        restProductImageMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImageSearchRepository.save(productImage);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());

        // Update the productImage
        ProductImage updatedProductImage = productImageRepository.findById(productImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductImage are not directly saved in db
        em.detach(updatedProductImage);
        updatedProductImage.imageUri(UPDATED_IMAGE_URI).altText(UPDATED_ALT_TEXT);
        ProductImageDTO productImageDTO = productImageMapper.toDto(updatedProductImage);

        restProductImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductImageToMatchAllProperties(updatedProductImage);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductImage> productImageSearchList = Streamable.of(productImageSearchRepository.findAll()).toList();
                ProductImage testProductImageSearch = productImageSearchList.get(searchDatabaseSizeAfter - 1);

                assertProductImageAllPropertiesEquals(testProductImageSearch, updatedProductImage);
            });
    }

    @Test
    @Transactional
    void putNonExistingProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        productImage.setId(UUID.randomUUID());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        productImage.setId(UUID.randomUUID());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        productImage.setId(UUID.randomUUID());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProductImageWithPatch() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productImage using partial update
        ProductImage partialUpdatedProductImage = new ProductImage();
        partialUpdatedProductImage.setId(productImage.getId());

        partialUpdatedProductImage.imageUri(UPDATED_IMAGE_URI);

        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductImage))
            )
            .andExpect(status().isOk());

        // Validate the ProductImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductImageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductImage, productImage),
            getPersistedProductImage(productImage)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductImageWithPatch() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productImage using partial update
        ProductImage partialUpdatedProductImage = new ProductImage();
        partialUpdatedProductImage.setId(productImage.getId());

        partialUpdatedProductImage.imageUri(UPDATED_IMAGE_URI).altText(UPDATED_ALT_TEXT);

        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductImage))
            )
            .andExpect(status().isOk());

        // Validate the ProductImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductImageUpdatableFieldsEquals(partialUpdatedProductImage, getPersistedProductImage(partialUpdatedProductImage));
    }

    @Test
    @Transactional
    void patchNonExistingProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        productImage.setId(UUID.randomUUID());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        productImage.setId(UUID.randomUUID());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        productImage.setId(UUID.randomUUID());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);
        productImageRepository.save(productImage);
        productImageSearchRepository.save(productImage);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productImage
        restProductImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, productImage.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);
        productImageSearchRepository.save(productImage);

        // Search the productImage
        restProductImageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + productImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productImage.getId().toString())))
            .andExpect(jsonPath("$.[*].imageUri").value(hasItem(DEFAULT_IMAGE_URI)))
            .andExpect(jsonPath("$.[*].altText").value(hasItem(DEFAULT_ALT_TEXT)));
    }

    protected long getRepositoryCount() {
        return productImageRepository.count();
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

    protected ProductImage getPersistedProductImage(ProductImage productImage) {
        return productImageRepository.findById(productImage.getId()).orElseThrow();
    }

    protected void assertPersistedProductImageToMatchAllProperties(ProductImage expectedProductImage) {
        assertProductImageAllPropertiesEquals(expectedProductImage, getPersistedProductImage(expectedProductImage));
    }

    protected void assertPersistedProductImageToMatchUpdatableProperties(ProductImage expectedProductImage) {
        assertProductImageAllUpdatablePropertiesEquals(expectedProductImage, getPersistedProductImage(expectedProductImage));
    }
}
