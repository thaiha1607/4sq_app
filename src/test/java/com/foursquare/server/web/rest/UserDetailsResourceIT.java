package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.UserDetailsAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.UserDetails;
import com.foursquare.server.repository.UserDetailsRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.service.UserDetailsService;
import com.foursquare.server.service.dto.UserDetailsDTO;
import com.foursquare.server.service.mapper.UserDetailsMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
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
 * Integration tests for the {@link UserDetailsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserDetailsResourceIT {

    private static final String DEFAULT_PHONE = "+06098929020021";
    private static final String UPDATED_PHONE = "+29173231";

    private static final String ENTITY_API_URL = "/api/user-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserDetailsRepository userDetailsRepositoryMock;

    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Mock
    private UserDetailsService userDetailsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDetailsMockMvc;

    private UserDetails userDetails;

    private UserDetails insertedUserDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserDetails createEntity(EntityManager em) {
        UserDetails userDetails = new UserDetails().phone(DEFAULT_PHONE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userDetails.setUser(user);
        return userDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserDetails createUpdatedEntity(EntityManager em) {
        UserDetails userDetails = new UserDetails().phone(UPDATED_PHONE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userDetails.setUser(user);
        return userDetails;
    }

    @BeforeEach
    public void initTest() {
        userDetails = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserDetails != null) {
            userDetailsRepository.delete(insertedUserDetails);
            insertedUserDetails = null;
        }
    }

    @Test
    @Transactional
    void createUserDetails() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);
        var returnedUserDetailsDTO = om.readValue(
            restUserDetailsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetailsDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserDetailsDTO.class
        );

        // Validate the UserDetails in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserDetails = userDetailsMapper.toEntity(returnedUserDetailsDTO);
        assertUserDetailsUpdatableFieldsEquals(returnedUserDetails, getPersistedUserDetails(returnedUserDetails));

        insertedUserDetails = returnedUserDetails;
    }

    @Test
    @Transactional
    void createUserDetailsWithExistingId() throws Exception {
        // Create the UserDetails with an existing ID
        userDetails.setId(1L);
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDetailsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetailsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserDetails() throws Exception {
        // Initialize the database
        insertedUserDetails = userDetailsRepository.saveAndFlush(userDetails);

        // Get all the userDetailsList
        restUserDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userDetailsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userDetailsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserDetails() throws Exception {
        // Initialize the database
        insertedUserDetails = userDetailsRepository.saveAndFlush(userDetails);

        // Get the userDetails
        restUserDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, userDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userDetails.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getNonExistingUserDetails() throws Exception {
        // Get the userDetails
        restUserDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserDetails() throws Exception {
        // Initialize the database
        insertedUserDetails = userDetailsRepository.saveAndFlush(userDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userDetails
        UserDetails updatedUserDetails = userDetailsRepository.findById(userDetails.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserDetails are not directly saved in db
        em.detach(updatedUserDetails);
        updatedUserDetails.phone(UPDATED_PHONE);
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(updatedUserDetails);

        restUserDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDetailsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDetailsDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserDetailsToMatchAllProperties(updatedUserDetails);
    }

    @Test
    @Transactional
    void putNonExistingUserDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetails.setId(longCount.incrementAndGet());

        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDetailsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetails.setId(longCount.incrementAndGet());

        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetails.setId(longCount.incrementAndGet());

        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDetailsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDetailsWithPatch() throws Exception {
        // Initialize the database
        insertedUserDetails = userDetailsRepository.saveAndFlush(userDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userDetails using partial update
        UserDetails partialUpdatedUserDetails = new UserDetails();
        partialUpdatedUserDetails.setId(userDetails.getId());

        restUserDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserDetails))
            )
            .andExpect(status().isOk());

        // Validate the UserDetails in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDetailsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserDetails, userDetails),
            getPersistedUserDetails(userDetails)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserDetailsWithPatch() throws Exception {
        // Initialize the database
        insertedUserDetails = userDetailsRepository.saveAndFlush(userDetails);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userDetails using partial update
        UserDetails partialUpdatedUserDetails = new UserDetails();
        partialUpdatedUserDetails.setId(userDetails.getId());

        partialUpdatedUserDetails.phone(UPDATED_PHONE);

        restUserDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserDetails))
            )
            .andExpect(status().isOk());

        // Validate the UserDetails in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDetailsUpdatableFieldsEquals(partialUpdatedUserDetails, getPersistedUserDetails(partialUpdatedUserDetails));
    }

    @Test
    @Transactional
    void patchNonExistingUserDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetails.setId(longCount.incrementAndGet());

        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDetailsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetails.setId(longCount.incrementAndGet());

        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserDetails() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userDetails.setId(longCount.incrementAndGet());

        // Create the UserDetails
        UserDetailsDTO userDetailsDTO = userDetailsMapper.toDto(userDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDetailsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDetailsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserDetails in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserDetails() throws Exception {
        // Initialize the database
        insertedUserDetails = userDetailsRepository.saveAndFlush(userDetails);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userDetails
        restUserDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, userDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userDetailsRepository.count();
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

    protected UserDetails getPersistedUserDetails(UserDetails userDetails) {
        return userDetailsRepository.findById(userDetails.getId()).orElseThrow();
    }

    protected void assertPersistedUserDetailsToMatchAllProperties(UserDetails expectedUserDetails) {
        assertUserDetailsAllPropertiesEquals(expectedUserDetails, getPersistedUserDetails(expectedUserDetails));
    }

    protected void assertPersistedUserDetailsToMatchUpdatableProperties(UserDetails expectedUserDetails) {
        assertUserDetailsAllUpdatablePropertiesEquals(expectedUserDetails, getPersistedUserDetails(expectedUserDetails));
    }
}
