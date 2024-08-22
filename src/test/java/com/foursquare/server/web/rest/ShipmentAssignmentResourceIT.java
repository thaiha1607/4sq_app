package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.ShipmentAssignmentAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.enumeration.AssignmentStatus;
import com.foursquare.server.repository.ShipmentAssignmentRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.service.ShipmentAssignmentService;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.service.mapper.ShipmentAssignmentMapper;
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

    private static final String DEFAULT_OTHER_INFO = "AAAAAAAAAA";
    private static final String UPDATED_OTHER_INFO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shipment-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

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
        ShipmentAssignment shipmentAssignment = new ShipmentAssignment()
            .status(DEFAULT_STATUS)
            .note(DEFAULT_NOTE)
            .otherInfo(DEFAULT_OTHER_INFO);
        return shipmentAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentAssignment createUpdatedEntity(EntityManager em) {
        ShipmentAssignment shipmentAssignment = new ShipmentAssignment()
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE)
            .otherInfo(UPDATED_OTHER_INFO);
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
            insertedShipmentAssignment = null;
        }
    }

    @Test
    @Transactional
    void createShipmentAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
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

        insertedShipmentAssignment = returnedShipmentAssignment;
    }

    @Test
    @Transactional
    void createShipmentAssignmentWithExistingId() throws Exception {
        // Create the ShipmentAssignment with an existing ID
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipmentAssignment.setStatus(null);

        // Create the ShipmentAssignment, which fails.
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        restShipmentAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
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
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].otherInfo").value(hasItem(DEFAULT_OTHER_INFO)));
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
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.otherInfo").value(DEFAULT_OTHER_INFO));
    }

    @Test
    @Transactional
    void getShipmentAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        UUID id = shipmentAssignment.getId();

        defaultShipmentAssignmentFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where status equals to
        defaultShipmentAssignmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where status in
        defaultShipmentAssignmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where status is not null
        defaultShipmentAssignmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where note equals to
        defaultShipmentAssignmentFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where note in
        defaultShipmentAssignmentFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where note is not null
        defaultShipmentAssignmentFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where note contains
        defaultShipmentAssignmentFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where note does not contain
        defaultShipmentAssignmentFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByOtherInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where otherInfo equals to
        defaultShipmentAssignmentFiltering("otherInfo.equals=" + DEFAULT_OTHER_INFO, "otherInfo.equals=" + UPDATED_OTHER_INFO);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByOtherInfoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where otherInfo in
        defaultShipmentAssignmentFiltering(
            "otherInfo.in=" + DEFAULT_OTHER_INFO + "," + UPDATED_OTHER_INFO,
            "otherInfo.in=" + UPDATED_OTHER_INFO
        );
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByOtherInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where otherInfo is not null
        defaultShipmentAssignmentFiltering("otherInfo.specified=true", "otherInfo.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByOtherInfoContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where otherInfo contains
        defaultShipmentAssignmentFiltering("otherInfo.contains=" + DEFAULT_OTHER_INFO, "otherInfo.contains=" + UPDATED_OTHER_INFO);
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByOtherInfoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        // Get all the shipmentAssignmentList where otherInfo does not contain
        defaultShipmentAssignmentFiltering(
            "otherInfo.doesNotContain=" + UPDATED_OTHER_INFO,
            "otherInfo.doesNotContain=" + DEFAULT_OTHER_INFO
        );
    }

    @Test
    @Transactional
    void getAllShipmentAssignmentsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        shipmentAssignment.setUser(user);
        shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);
        Long userId = user.getId();
        // Get all the shipmentAssignmentList where user equals to userId
        defaultShipmentAssignmentShouldBeFound("userId.equals=" + userId);

        // Get all the shipmentAssignmentList where user equals to (userId + 1)
        defaultShipmentAssignmentShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultShipmentAssignmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShipmentAssignmentShouldBeFound(shouldBeFound);
        defaultShipmentAssignmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentAssignmentShouldBeFound(String filter) throws Exception {
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentAssignment.getId().toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].otherInfo").value(hasItem(DEFAULT_OTHER_INFO)));

        // Check, that the count call also returns 1
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentAssignmentShouldNotBeFound(String filter) throws Exception {
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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

        // Update the shipmentAssignment
        ShipmentAssignment updatedShipmentAssignment = shipmentAssignmentRepository.findById(shipmentAssignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentAssignment are not directly saved in db
        em.detach(updatedShipmentAssignment);
        updatedShipmentAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE).otherInfo(UPDATED_OTHER_INFO);
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
    }

    @Test
    @Transactional
    void putNonExistingShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
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

        partialUpdatedShipmentAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE).otherInfo(UPDATED_OTHER_INFO);

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

        partialUpdatedShipmentAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE).otherInfo(UPDATED_OTHER_INFO);

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
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
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
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentAssignment.setId(UUID.randomUUID());

        // Create the ShipmentAssignment
        ShipmentAssignmentDTO shipmentAssignmentDTO = shipmentAssignmentMapper.toDto(shipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentAssignmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipmentAssignment() throws Exception {
        // Initialize the database
        insertedShipmentAssignment = shipmentAssignmentRepository.saveAndFlush(shipmentAssignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipmentAssignment
        restShipmentAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentAssignment.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
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
