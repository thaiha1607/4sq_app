package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.WarehouseAssignmentAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.WarehouseAssignment;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.domain.enumeration.AssignmentStatus;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.repository.WarehouseAssignmentRepository;
import com.foursquare.server.service.WarehouseAssignmentService;
import com.foursquare.server.service.dto.WarehouseAssignmentDTO;
import com.foursquare.server.service.mapper.WarehouseAssignmentMapper;
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
 * Integration tests for the {@link WarehouseAssignmentResource} REST controller.
 */
@IntegrationTest
@Disabled("Cyclic required relationships detected")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WarehouseAssignmentResourceIT {

    private static final AssignmentStatus DEFAULT_STATUS = AssignmentStatus.PENDING;
    private static final AssignmentStatus UPDATED_STATUS = AssignmentStatus.ASSIGNED;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_OTHER_INFO = "AAAAAAAAAA";
    private static final String UPDATED_OTHER_INFO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/warehouse-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseAssignmentRepository warehouseAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private WarehouseAssignmentRepository warehouseAssignmentRepositoryMock;

    @Autowired
    private WarehouseAssignmentMapper warehouseAssignmentMapper;

    @Mock
    private WarehouseAssignmentService warehouseAssignmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseAssignmentMockMvc;

    private WarehouseAssignment warehouseAssignment;

    private WarehouseAssignment insertedWarehouseAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseAssignment createEntity(EntityManager em) {
        WarehouseAssignment warehouseAssignment = new WarehouseAssignment()
            .status(DEFAULT_STATUS)
            .note(DEFAULT_NOTE)
            .otherInfo(DEFAULT_OTHER_INFO);
        // Add required entity
        WorkingUnit workingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            workingUnit = WorkingUnitResourceIT.createEntity(em);
            em.persist(workingUnit);
            em.flush();
        } else {
            workingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        warehouseAssignment.setSourceWorkingUnit(workingUnit);
        return warehouseAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WarehouseAssignment createUpdatedEntity(EntityManager em) {
        WarehouseAssignment warehouseAssignment = new WarehouseAssignment()
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE)
            .otherInfo(UPDATED_OTHER_INFO);
        // Add required entity
        WorkingUnit workingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            workingUnit = WorkingUnitResourceIT.createUpdatedEntity(em);
            em.persist(workingUnit);
            em.flush();
        } else {
            workingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        warehouseAssignment.setSourceWorkingUnit(workingUnit);
        return warehouseAssignment;
    }

    @BeforeEach
    public void initTest() {
        warehouseAssignment = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedWarehouseAssignment != null) {
            warehouseAssignmentRepository.delete(insertedWarehouseAssignment);
            insertedWarehouseAssignment = null;
        }
    }

    @Test
    @Transactional
    void createWarehouseAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);
        var returnedWarehouseAssignmentDTO = om.readValue(
            restWarehouseAssignmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseAssignmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseAssignmentDTO.class
        );

        // Validate the WarehouseAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouseAssignment = warehouseAssignmentMapper.toEntity(returnedWarehouseAssignmentDTO);
        assertWarehouseAssignmentUpdatableFieldsEquals(
            returnedWarehouseAssignment,
            getPersistedWarehouseAssignment(returnedWarehouseAssignment)
        );

        insertedWarehouseAssignment = returnedWarehouseAssignment;
    }

    @Test
    @Transactional
    void createWarehouseAssignmentWithExistingId() throws Exception {
        // Create the WarehouseAssignment with an existing ID
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseAssignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        warehouseAssignment.setStatus(null);

        // Create the WarehouseAssignment, which fails.
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        restWarehouseAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseAssignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignments() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList
        restWarehouseAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouseAssignment.getId().toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].otherInfo").value(hasItem(DEFAULT_OTHER_INFO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWarehouseAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(warehouseAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWarehouseAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(warehouseAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWarehouseAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(warehouseAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWarehouseAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(warehouseAssignmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWarehouseAssignment() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get the warehouseAssignment
        restWarehouseAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouseAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(warehouseAssignment.getId().toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.otherInfo").value(DEFAULT_OTHER_INFO));
    }

    @Test
    @Transactional
    void getWarehouseAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        UUID id = warehouseAssignment.getId();

        defaultWarehouseAssignmentFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where status equals to
        defaultWarehouseAssignmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where status in
        defaultWarehouseAssignmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where status is not null
        defaultWarehouseAssignmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where note equals to
        defaultWarehouseAssignmentFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where note in
        defaultWarehouseAssignmentFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where note is not null
        defaultWarehouseAssignmentFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where note contains
        defaultWarehouseAssignmentFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where note does not contain
        defaultWarehouseAssignmentFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByOtherInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where otherInfo equals to
        defaultWarehouseAssignmentFiltering("otherInfo.equals=" + DEFAULT_OTHER_INFO, "otherInfo.equals=" + UPDATED_OTHER_INFO);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByOtherInfoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where otherInfo in
        defaultWarehouseAssignmentFiltering(
            "otherInfo.in=" + DEFAULT_OTHER_INFO + "," + UPDATED_OTHER_INFO,
            "otherInfo.in=" + UPDATED_OTHER_INFO
        );
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByOtherInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where otherInfo is not null
        defaultWarehouseAssignmentFiltering("otherInfo.specified=true", "otherInfo.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByOtherInfoContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where otherInfo contains
        defaultWarehouseAssignmentFiltering("otherInfo.contains=" + DEFAULT_OTHER_INFO, "otherInfo.contains=" + UPDATED_OTHER_INFO);
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByOtherInfoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        // Get all the warehouseAssignmentList where otherInfo does not contain
        defaultWarehouseAssignmentFiltering(
            "otherInfo.doesNotContain=" + UPDATED_OTHER_INFO,
            "otherInfo.doesNotContain=" + DEFAULT_OTHER_INFO
        );
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        warehouseAssignment.setUser(user);
        warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
        Long userId = user.getId();
        // Get all the warehouseAssignmentList where user equals to userId
        defaultWarehouseAssignmentShouldBeFound("userId.equals=" + userId);

        // Get all the warehouseAssignmentList where user equals to (userId + 1)
        defaultWarehouseAssignmentShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsBySourceWorkingUnitIsEqualToSomething() throws Exception {
        WorkingUnit sourceWorkingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
            sourceWorkingUnit = WorkingUnitResourceIT.createEntity(em);
        } else {
            sourceWorkingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        em.persist(sourceWorkingUnit);
        em.flush();
        warehouseAssignment.setSourceWorkingUnit(sourceWorkingUnit);
        warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
        UUID sourceWorkingUnitId = sourceWorkingUnit.getId();
        // Get all the warehouseAssignmentList where sourceWorkingUnit equals to sourceWorkingUnitId
        defaultWarehouseAssignmentShouldBeFound("sourceWorkingUnitId.equals=" + sourceWorkingUnitId);

        // Get all the warehouseAssignmentList where sourceWorkingUnit equals to UUID.randomUUID()
        defaultWarehouseAssignmentShouldNotBeFound("sourceWorkingUnitId.equals=" + UUID.randomUUID());
    }

    @Test
    @Transactional
    void getAllWarehouseAssignmentsByTargetWorkingUnitIsEqualToSomething() throws Exception {
        WorkingUnit targetWorkingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
            targetWorkingUnit = WorkingUnitResourceIT.createEntity(em);
        } else {
            targetWorkingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        em.persist(targetWorkingUnit);
        em.flush();
        warehouseAssignment.setTargetWorkingUnit(targetWorkingUnit);
        warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);
        UUID targetWorkingUnitId = targetWorkingUnit.getId();
        // Get all the warehouseAssignmentList where targetWorkingUnit equals to targetWorkingUnitId
        defaultWarehouseAssignmentShouldBeFound("targetWorkingUnitId.equals=" + targetWorkingUnitId);

        // Get all the warehouseAssignmentList where targetWorkingUnit equals to UUID.randomUUID()
        defaultWarehouseAssignmentShouldNotBeFound("targetWorkingUnitId.equals=" + UUID.randomUUID());
    }

    private void defaultWarehouseAssignmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWarehouseAssignmentShouldBeFound(shouldBeFound);
        defaultWarehouseAssignmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWarehouseAssignmentShouldBeFound(String filter) throws Exception {
        restWarehouseAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouseAssignment.getId().toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].otherInfo").value(hasItem(DEFAULT_OTHER_INFO)));

        // Check, that the count call also returns 1
        restWarehouseAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWarehouseAssignmentShouldNotBeFound(String filter) throws Exception {
        restWarehouseAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWarehouseAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWarehouseAssignment() throws Exception {
        // Get the warehouseAssignment
        restWarehouseAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouseAssignment() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseAssignment
        WarehouseAssignment updatedWarehouseAssignment = warehouseAssignmentRepository.findById(warehouseAssignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWarehouseAssignment are not directly saved in db
        em.detach(updatedWarehouseAssignment);
        updatedWarehouseAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE).otherInfo(UPDATED_OTHER_INFO);
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(updatedWarehouseAssignment);

        restWarehouseAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseAssignmentToMatchAllProperties(updatedWarehouseAssignment);
    }

    @Test
    @Transactional
    void putNonExistingWarehouseAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseAssignment.setId(UUID.randomUUID());

        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouseAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseAssignment.setId(UUID.randomUUID());

        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouseAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseAssignment.setId(UUID.randomUUID());

        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseAssignment using partial update
        WarehouseAssignment partialUpdatedWarehouseAssignment = new WarehouseAssignment();
        partialUpdatedWarehouseAssignment.setId(warehouseAssignment.getId());

        partialUpdatedWarehouseAssignment.status(UPDATED_STATUS);

        restWarehouseAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouseAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWarehouseAssignment))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWarehouseAssignment, warehouseAssignment),
            getPersistedWarehouseAssignment(warehouseAssignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouseAssignment using partial update
        WarehouseAssignment partialUpdatedWarehouseAssignment = new WarehouseAssignment();
        partialUpdatedWarehouseAssignment.setId(warehouseAssignment.getId());

        partialUpdatedWarehouseAssignment.status(UPDATED_STATUS).note(UPDATED_NOTE).otherInfo(UPDATED_OTHER_INFO);

        restWarehouseAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouseAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWarehouseAssignment))
            )
            .andExpect(status().isOk());

        // Validate the WarehouseAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseAssignmentUpdatableFieldsEquals(
            partialUpdatedWarehouseAssignment,
            getPersistedWarehouseAssignment(partialUpdatedWarehouseAssignment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingWarehouseAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseAssignment.setId(UUID.randomUUID());

        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseAssignmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouseAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseAssignment.setId(UUID.randomUUID());

        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouseAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouseAssignment.setId(UUID.randomUUID());

        // Create the WarehouseAssignment
        WarehouseAssignmentDTO warehouseAssignmentDTO = warehouseAssignmentMapper.toDto(warehouseAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(warehouseAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WarehouseAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouseAssignment() throws Exception {
        // Initialize the database
        insertedWarehouseAssignment = warehouseAssignmentRepository.saveAndFlush(warehouseAssignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the warehouseAssignment
        restWarehouseAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, warehouseAssignment.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return warehouseAssignmentRepository.count();
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

    protected WarehouseAssignment getPersistedWarehouseAssignment(WarehouseAssignment warehouseAssignment) {
        return warehouseAssignmentRepository.findById(warehouseAssignment.getId()).orElseThrow();
    }

    protected void assertPersistedWarehouseAssignmentToMatchAllProperties(WarehouseAssignment expectedWarehouseAssignment) {
        assertWarehouseAssignmentAllPropertiesEquals(
            expectedWarehouseAssignment,
            getPersistedWarehouseAssignment(expectedWarehouseAssignment)
        );
    }

    protected void assertPersistedWarehouseAssignmentToMatchUpdatableProperties(WarehouseAssignment expectedWarehouseAssignment) {
        assertWarehouseAssignmentAllUpdatablePropertiesEquals(
            expectedWarehouseAssignment,
            getPersistedWarehouseAssignment(expectedWarehouseAssignment)
        );
    }
}
