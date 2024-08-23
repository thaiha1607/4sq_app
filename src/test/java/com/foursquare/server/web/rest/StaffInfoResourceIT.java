package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.StaffInfoAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.StaffInfo;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.domain.enumeration.StaffRole;
import com.foursquare.server.domain.enumeration.StaffStatus;
import com.foursquare.server.repository.StaffInfoRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.service.StaffInfoService;
import com.foursquare.server.service.dto.StaffInfoDTO;
import com.foursquare.server.service.mapper.StaffInfoMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link StaffInfoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StaffInfoResourceIT {

    private static final StaffStatus DEFAULT_STATUS = StaffStatus.ACTIVE;
    private static final StaffStatus UPDATED_STATUS = StaffStatus.INACTIVE;

    private static final StaffRole DEFAULT_ROLE = StaffRole.SALESPERSON;
    private static final StaffRole UPDATED_ROLE = StaffRole.WAREHOUSE;

    private static final String ENTITY_API_URL = "/api/staff-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StaffInfoRepository staffInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StaffInfoRepository staffInfoRepositoryMock;

    @Autowired
    private StaffInfoMapper staffInfoMapper;

    @Mock
    private StaffInfoService staffInfoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStaffInfoMockMvc;

    private StaffInfo staffInfo;

    private StaffInfo insertedStaffInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StaffInfo createEntity(EntityManager em) {
        StaffInfo staffInfo = new StaffInfo().status(DEFAULT_STATUS).role(DEFAULT_ROLE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        staffInfo.setUser(user);
        return staffInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StaffInfo createUpdatedEntity(EntityManager em) {
        StaffInfo staffInfo = new StaffInfo().status(UPDATED_STATUS).role(UPDATED_ROLE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        staffInfo.setUser(user);
        return staffInfo;
    }

    @BeforeEach
    public void initTest() {
        staffInfo = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedStaffInfo != null) {
            staffInfoRepository.delete(insertedStaffInfo);
            insertedStaffInfo = null;
        }
    }

    @Test
    @Transactional
    void createStaffInfo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);
        var returnedStaffInfoDTO = om.readValue(
            restStaffInfoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffInfoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StaffInfoDTO.class
        );

        // Validate the StaffInfo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStaffInfo = staffInfoMapper.toEntity(returnedStaffInfoDTO);
        assertStaffInfoUpdatableFieldsEquals(returnedStaffInfo, getPersistedStaffInfo(returnedStaffInfo));

        insertedStaffInfo = returnedStaffInfo;
    }

    @Test
    @Transactional
    void createStaffInfoWithExistingId() throws Exception {
        // Create the StaffInfo with an existing ID
        staffInfo.setId(1L);
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaffInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        staffInfo.setStatus(null);

        // Create the StaffInfo, which fails.
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        restStaffInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffInfoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        staffInfo.setRole(null);

        // Create the StaffInfo, which fails.
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        restStaffInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffInfoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStaffInfos() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList
        restStaffInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staffInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStaffInfosWithEagerRelationshipsIsEnabled() throws Exception {
        when(staffInfoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStaffInfoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(staffInfoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStaffInfosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(staffInfoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStaffInfoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(staffInfoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStaffInfo() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get the staffInfo
        restStaffInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, staffInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(staffInfo.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    void getStaffInfosByIdFiltering() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        Long id = staffInfo.getId();

        defaultStaffInfoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStaffInfoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStaffInfoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStaffInfosByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList where status equals to
        defaultStaffInfoFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStaffInfosByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList where status in
        defaultStaffInfoFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStaffInfosByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList where status is not null
        defaultStaffInfoFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffInfosByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList where role equals to
        defaultStaffInfoFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllStaffInfosByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList where role in
        defaultStaffInfoFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllStaffInfosByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        // Get all the staffInfoList where role is not null
        defaultStaffInfoFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffInfosByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = staffInfo.getUser();
        staffInfoRepository.saveAndFlush(staffInfo);
        Long userId = user.getId();
        // Get all the staffInfoList where user equals to userId
        defaultStaffInfoShouldBeFound("userId.equals=" + userId);

        // Get all the staffInfoList where user equals to (userId + 1)
        defaultStaffInfoShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllStaffInfosByWorkingUnitIsEqualToSomething() throws Exception {
        WorkingUnit workingUnit;
        if (TestUtil.findAll(em, WorkingUnit.class).isEmpty()) {
            staffInfoRepository.saveAndFlush(staffInfo);
            workingUnit = WorkingUnitResourceIT.createEntity(em);
        } else {
            workingUnit = TestUtil.findAll(em, WorkingUnit.class).get(0);
        }
        em.persist(workingUnit);
        em.flush();
        staffInfo.setWorkingUnit(workingUnit);
        staffInfoRepository.saveAndFlush(staffInfo);
        UUID workingUnitId = workingUnit.getId();
        // Get all the staffInfoList where workingUnit equals to workingUnitId
        defaultStaffInfoShouldBeFound("workingUnitId.equals=" + workingUnitId);

        // Get all the staffInfoList where workingUnit equals to UUID.randomUUID()
        defaultStaffInfoShouldNotBeFound("workingUnitId.equals=" + UUID.randomUUID());
    }

    private void defaultStaffInfoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStaffInfoShouldBeFound(shouldBeFound);
        defaultStaffInfoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStaffInfoShouldBeFound(String filter) throws Exception {
        restStaffInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staffInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));

        // Check, that the count call also returns 1
        restStaffInfoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStaffInfoShouldNotBeFound(String filter) throws Exception {
        restStaffInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStaffInfoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStaffInfo() throws Exception {
        // Get the staffInfo
        restStaffInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStaffInfo() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staffInfo
        StaffInfo updatedStaffInfo = staffInfoRepository.findById(staffInfo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStaffInfo are not directly saved in db
        em.detach(updatedStaffInfo);
        updatedStaffInfo.status(UPDATED_STATUS).role(UPDATED_ROLE);
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(updatedStaffInfo);

        restStaffInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffInfoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffInfoDTO))
            )
            .andExpect(status().isOk());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStaffInfoToMatchAllProperties(updatedStaffInfo);
    }

    @Test
    @Transactional
    void putNonExistingStaffInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staffInfo.setId(longCount.incrementAndGet());

        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffInfoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStaffInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staffInfo.setId(longCount.incrementAndGet());

        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStaffInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staffInfo.setId(longCount.incrementAndGet());

        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffInfoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStaffInfoWithPatch() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staffInfo using partial update
        StaffInfo partialUpdatedStaffInfo = new StaffInfo();
        partialUpdatedStaffInfo.setId(staffInfo.getId());

        partialUpdatedStaffInfo.status(UPDATED_STATUS).role(UPDATED_ROLE);

        restStaffInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaffInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStaffInfo))
            )
            .andExpect(status().isOk());

        // Validate the StaffInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaffInfoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStaffInfo, staffInfo),
            getPersistedStaffInfo(staffInfo)
        );
    }

    @Test
    @Transactional
    void fullUpdateStaffInfoWithPatch() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staffInfo using partial update
        StaffInfo partialUpdatedStaffInfo = new StaffInfo();
        partialUpdatedStaffInfo.setId(staffInfo.getId());

        partialUpdatedStaffInfo.status(UPDATED_STATUS).role(UPDATED_ROLE);

        restStaffInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaffInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStaffInfo))
            )
            .andExpect(status().isOk());

        // Validate the StaffInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaffInfoUpdatableFieldsEquals(partialUpdatedStaffInfo, getPersistedStaffInfo(partialUpdatedStaffInfo));
    }

    @Test
    @Transactional
    void patchNonExistingStaffInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staffInfo.setId(longCount.incrementAndGet());

        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, staffInfoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(staffInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStaffInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staffInfo.setId(longCount.incrementAndGet());

        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(staffInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStaffInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staffInfo.setId(longCount.incrementAndGet());

        // Create the StaffInfo
        StaffInfoDTO staffInfoDTO = staffInfoMapper.toDto(staffInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffInfoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(staffInfoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StaffInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStaffInfo() throws Exception {
        // Initialize the database
        insertedStaffInfo = staffInfoRepository.saveAndFlush(staffInfo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the staffInfo
        restStaffInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, staffInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return staffInfoRepository.count();
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

    protected StaffInfo getPersistedStaffInfo(StaffInfo staffInfo) {
        return staffInfoRepository.findById(staffInfo.getId()).orElseThrow();
    }

    protected void assertPersistedStaffInfoToMatchAllProperties(StaffInfo expectedStaffInfo) {
        assertStaffInfoAllPropertiesEquals(expectedStaffInfo, getPersistedStaffInfo(expectedStaffInfo));
    }

    protected void assertPersistedStaffInfoToMatchUpdatableProperties(StaffInfo expectedStaffInfo) {
        assertStaffInfoAllUpdatablePropertiesEquals(expectedStaffInfo, getPersistedStaffInfo(expectedStaffInfo));
    }
}
