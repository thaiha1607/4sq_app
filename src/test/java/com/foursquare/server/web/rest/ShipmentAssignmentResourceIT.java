package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ShipmentAssignmentAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.domain.enumeration.AssignmentStatus;
import com.foursquare.server.repository.ShipmentAssignmentRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.repository.search.ShipmentAssignmentSearchRepository;
import com.foursquare.server.service.ShipmentAssignmentService;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.service.mapper.ShipmentAssignmentMapper;
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
 * Integration tests for the {@link ShipmentAssignmentResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ShipmentAssignmentResourceIT {

    private static final AssignmentStatus DEFAULT_STATUS = AssignmentStatus.PENDING;
    private static final AssignmentStatus UPDATED_STATUS = AssignmentStatus.ASSIGNED;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shipment-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/shipment-assignments/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentAssignmentRepository shipmentAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ShipmentAssignmentRepository shipmentAssignmentRepositoryMock;

    @Autowired
    private ShipmentAssignmentMapper shipmentAssignmentMapper;

    @Mock
    private ShipmentAssignmentService shipmentAssignmentServiceMock;

    @Autowired
    private ShipmentAssignmentSearchRepository shipmentAssignmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentAssignmentMockMvc;

    private ShipmentAssignment shipmentAssignment;

    private ShipmentAssignment insertedShipmentAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentAssignment createEntity(EntityManager em) {
        ShipmentAssignment shipmentAssignment = new ShipmentAssignment().status(DEFAULT_STATUS).note(DEFAULT_NOTE);
        return shipmentAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentAssignment createUpdatedEntity(EntityManager em) {
        ShipmentAssignment shipmentAssignment = new ShipmentAssignment().status(UPDATED_STATUS).note(UPDATED_NOTE);
        return shipmentAssignment;
    }

    @BeforeEach
    public void initTest() {
        shipmentAssignment = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedShipmentAssignment != null) {
            shipmentAssignmentRepository.delete(insertedShipmentAssignment);
            shipmentAssignmentSearchRepository.delete(insertedShipmentAssignment);
            insertedShipmentAssignment = null;
        }
    }

    @Test
    @Transactional
    void createShipmentAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);
        var returnedShipmentAssignmentDTO = om.readValue(
            restShipmentAssignmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentAssignmentDTO.class
        );

        // Validate the ShipmentAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipmentAssignment = shipmentAssignmentMapper.toEntity(returnedShipmentAssignmentDTO);
        assertShipmentAssignmentUpdatableFieldsEquals(
            returnedShipmentAssignment,
            getPersistedShipmentAssignment(returnedShipmentAssignment)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedShipmentAssignment = returnedShipmentAssignment;
    }

    @Test
    @Transactional
    void createShipmentAssignmentWithExistingId() throws Exception {
        // Create the ShipmentAssignment with an existing ID
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        // set the field null
        shipmentAssignment.setStatus(null);

        // Create the ShipmentAssignment, which fails.
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        restShipmentAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllShipmentAssignments() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentAssignment.getId().toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShipmentAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(shipmentAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restShipmentAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(shipmentAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShipmentAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(shipmentAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restShipmentAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(shipmentAssignmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getShipmentAssignment() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get the shipmentAssignment
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, shipmentAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipmentAssignment.getId().toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingShipmentAssignment() throws Exception {
        // Get the shipmentAssignment
        restShipmentAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipmentAssignment() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentAssignmentSearchRepository.save(shipmentAssignment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());

        // Update the shipmentAssignment
        ShipmentAssignment updatedShipmentAssignment = shipmentAssignmentRepository.findById(shipmentAssignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentAssignment are not directly saved in db
        em.detach(updatedShipmentAssignment);
        updatedShipmentAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE);
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(updatedShipmentAssignment);

        restShipmentAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentAssignmentToMatchAllProperties(updatedShipmentAssignment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ShipmentAssignment> shipmentAssignmentSearchList = Streamable.of(
                    shipmentAssignmentSearchRepository.findAll()
                ).toList();
                ShipmentAssignment testShipmentAssignmentSearch = shipmentAssignmentSearchList.get(searchDatabaseSizeAfter - 1);

                assertShipmentAssignmentAllPropertiesEquals(testShipmentAssignmentSearch, updatedShipmentAssignment);
            });
    }

    @Test
    @Transactional
    void putNonExistingShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateShipmentAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentAssignment using partial update
        ShipmentAssignment partialUpdatedShipmentAssignment = new ShipmentAssignment();
        partialUpdatedShipmentAssignment.setId(shipmentAssignment.getId());

        partialUpdatedShipmentAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE);

        restShipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShipmentAssignment, shipmentAssignment),
            getPersistedShipmentAssignment(shipmentAssignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateShipmentAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentAssignment using partial update
        ShipmentAssignment partialUpdatedShipmentAssignment = new ShipmentAssignment();
        partialUpdatedShipmentAssignment.setId(shipmentAssignment.getId());

        partialUpdatedShipmentAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE);

        restShipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentAssignmentUpdatableFieldsEquals(
            partialUpdatedShipmentAssignment,
            getPersistedShipmentAssignment(partialUpdatedShipmentAssignment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentAssignmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteShipmentAssignment() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);
        shipmentAssignmentRepository.save(shipmentAssignment);
        shipmentAssignmentSearchRepository.save(shipmentAssignment);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the shipmentAssignment
        restShipmentAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentAssignment.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shipmentAssignmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchShipmentAssignment() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);
        shipmentAssignmentSearchRepository.save(shipmentAssignment);

        // Search the shipmentAssignment
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + shipmentAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentAssignment.getId().toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    protected long getRepositoryCount() {
        return shipmentAssignmentRepository.count();
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

    protected ShipmentAssignment getPersistedShipmentAssignment(ShipmentAssignment shipmentAssignment) {
        return shipmentAssignmentRepository.findById(shipmentAssignment.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentAssignmentToMatchAllProperties(ShipmentAssignment expectedShipmentAssignment) {
        assertShipmentAssignmentAllPropertiesEquals(expectedShipmentAssignment, getPersistedShipmentAssignment(expectedShipmentAssignment));
    }

    protected void assertPersistedShipmentAssignmentToMatchUpdatableProperties(ShipmentAssignment expectedShipmentAssignment) {
        assertShipmentAssignmentAllUpdatablePropertiesEquals(
            expectedShipmentAssignment,
            getPersistedShipmentAssignment(expectedShipmentAssignment)
        );
    }
}
