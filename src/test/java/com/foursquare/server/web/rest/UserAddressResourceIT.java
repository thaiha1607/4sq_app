package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.UserAddressAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Address;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.UserAddress;
import com.foursquare.server.domain.enumeration.AddressType;
import com.foursquare.server.repository.UserAddressRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.service.UserAddressService;
import com.foursquare.server.service.dto.UserAddressDTO;
import com.foursquare.server.service.mapper.UserAddressMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.UUID;
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
 * Integration tests for the {@link UserAddressResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserAddressResourceIT {

    private static final AddressType DEFAULT_TYPE = AddressType.HOME;
    private static final AddressType UPDATED_TYPE = AddressType.WORK;

    private static final String DEFAULT_FRIENDLY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FRIENDLY_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final String ENTITY_API_URL = "/api/user-addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserAddressRepository userAddressRepositoryMock;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Mock
    private UserAddressService userAddressServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAddressMockMvc;

    private UserAddress userAddress;

    private UserAddress insertedUserAddress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAddress createEntity(EntityManager em) {
        UserAddress userAddress = new UserAddress().type(DEFAULT_TYPE).friendlyName(DEFAULT_FRIENDLY_NAME).isDefault(DEFAULT_IS_DEFAULT);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userAddress.setUser(user);
        // Add required entity
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            address = AddressResourceIT.createEntity(em);
            em.persist(address);
            em.flush();
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        userAddress.setAddress(address);
        return userAddress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAddress createUpdatedEntity(EntityManager em) {
        UserAddress userAddress = new UserAddress().type(UPDATED_TYPE).friendlyName(UPDATED_FRIENDLY_NAME).isDefault(UPDATED_IS_DEFAULT);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userAddress.setUser(user);
        // Add required entity
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            address = AddressResourceIT.createUpdatedEntity(em);
            em.persist(address);
            em.flush();
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        userAddress.setAddress(address);
        return userAddress;
    }

    @BeforeEach
    public void initTest() {
        userAddress = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserAddress != null) {
            userAddressRepository.delete(insertedUserAddress);
            insertedUserAddress = null;
        }
    }

    @Test
    @Transactional
    void createUserAddress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);
        var returnedUserAddressDTO = om.readValue(
            restUserAddressMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserAddressDTO.class
        );

        // Validate the UserAddress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserAddress = userAddressMapper.toEntity(returnedUserAddressDTO);
        assertUserAddressUpdatableFieldsEquals(returnedUserAddress, getPersistedUserAddress(returnedUserAddress));

        insertedUserAddress = returnedUserAddress;
    }

    @Test
    @Transactional
    void createUserAddressWithExistingId() throws Exception {
        // Create the UserAddress with an existing ID
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAddressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAddress.setType(null);

        // Create the UserAddress, which fails.
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        restUserAddressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserAddresses() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList
        restUserAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAddress.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].friendlyName").value(hasItem(DEFAULT_FRIENDLY_NAME)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserAddressesWithEagerRelationshipsIsEnabled() throws Exception {
        when(userAddressServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserAddressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userAddressServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserAddressesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userAddressServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserAddressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userAddressRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserAddress() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get the userAddress
        restUserAddressMockMvc
            .perform(get(ENTITY_API_URL_ID, userAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAddress.getId().toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.friendlyName").value(DEFAULT_FRIENDLY_NAME))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT.booleanValue()));
    }

    @Test
    @Transactional
    void getUserAddressesByIdFiltering() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        UUID id = userAddress.getId();

        defaultUserAddressFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllUserAddressesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where type equals to
        defaultUserAddressFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllUserAddressesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where type in
        defaultUserAddressFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllUserAddressesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where type is not null
        defaultUserAddressFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllUserAddressesByFriendlyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where friendlyName equals to
        defaultUserAddressFiltering("friendlyName.equals=" + DEFAULT_FRIENDLY_NAME, "friendlyName.equals=" + UPDATED_FRIENDLY_NAME);
    }

    @Test
    @Transactional
    void getAllUserAddressesByFriendlyNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where friendlyName in
        defaultUserAddressFiltering(
            "friendlyName.in=" + DEFAULT_FRIENDLY_NAME + "," + UPDATED_FRIENDLY_NAME,
            "friendlyName.in=" + UPDATED_FRIENDLY_NAME
        );
    }

    @Test
    @Transactional
    void getAllUserAddressesByFriendlyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where friendlyName is not null
        defaultUserAddressFiltering("friendlyName.specified=true", "friendlyName.specified=false");
    }

    @Test
    @Transactional
    void getAllUserAddressesByFriendlyNameContainsSomething() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where friendlyName contains
        defaultUserAddressFiltering("friendlyName.contains=" + DEFAULT_FRIENDLY_NAME, "friendlyName.contains=" + UPDATED_FRIENDLY_NAME);
    }

    @Test
    @Transactional
    void getAllUserAddressesByFriendlyNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where friendlyName does not contain
        defaultUserAddressFiltering(
            "friendlyName.doesNotContain=" + UPDATED_FRIENDLY_NAME,
            "friendlyName.doesNotContain=" + DEFAULT_FRIENDLY_NAME
        );
    }

    @Test
    @Transactional
    void getAllUserAddressesByIsDefaultIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where isDefault equals to
        defaultUserAddressFiltering("isDefault.equals=" + DEFAULT_IS_DEFAULT, "isDefault.equals=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    void getAllUserAddressesByIsDefaultIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where isDefault in
        defaultUserAddressFiltering("isDefault.in=" + DEFAULT_IS_DEFAULT + "," + UPDATED_IS_DEFAULT, "isDefault.in=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    void getAllUserAddressesByIsDefaultIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        // Get all the userAddressList where isDefault is not null
        defaultUserAddressFiltering("isDefault.specified=true", "isDefault.specified=false");
    }

    @Test
    @Transactional
    void getAllUserAddressesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userAddressRepository.saveAndFlush(userAddress);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        userAddress.setUser(user);
        userAddressRepository.saveAndFlush(userAddress);
        Long userId = user.getId();
        // Get all the userAddressList where user equals to userId
        defaultUserAddressShouldBeFound("userId.equals=" + userId);

        // Get all the userAddressList where user equals to (userId + 1)
        defaultUserAddressShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllUserAddressesByAddressIsEqualToSomething() throws Exception {
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            userAddressRepository.saveAndFlush(userAddress);
            address = AddressResourceIT.createEntity(em);
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        em.persist(address);
        em.flush();
        userAddress.setAddress(address);
        userAddressRepository.saveAndFlush(userAddress);
        UUID addressId = address.getId();
        // Get all the userAddressList where address equals to addressId
        defaultUserAddressShouldBeFound("addressId.equals=" + addressId);

        // Get all the userAddressList where address equals to UUID.randomUUID()
        defaultUserAddressShouldNotBeFound("addressId.equals=" + UUID.randomUUID());
    }

    private void defaultUserAddressFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUserAddressShouldBeFound(shouldBeFound);
        defaultUserAddressShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserAddressShouldBeFound(String filter) throws Exception {
        restUserAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAddress.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].friendlyName").value(hasItem(DEFAULT_FRIENDLY_NAME)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())));

        // Check, that the count call also returns 1
        restUserAddressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserAddressShouldNotBeFound(String filter) throws Exception {
        restUserAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserAddressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserAddress() throws Exception {
        // Get the userAddress
        restUserAddressMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserAddress() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAddress
        UserAddress updatedUserAddress = userAddressRepository.findById(userAddress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserAddress are not directly saved in db
        em.detach(updatedUserAddress);
        updatedUserAddress.type(UPDATED_TYPE).friendlyName(UPDATED_FRIENDLY_NAME).isDefault(UPDATED_IS_DEFAULT);
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(updatedUserAddress);

        restUserAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAddressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAddressDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserAddressToMatchAllProperties(updatedUserAddress);
    }

    @Test
    @Transactional
    void putNonExistingUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAddressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserAddressWithPatch() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAddress using partial update
        UserAddress partialUpdatedUserAddress = new UserAddress();
        partialUpdatedUserAddress.setId(userAddress.getId());

        partialUpdatedUserAddress.type(UPDATED_TYPE).friendlyName(UPDATED_FRIENDLY_NAME);

        restUserAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAddress))
            )
            .andExpect(status().isOk());

        // Validate the UserAddress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAddressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserAddress, userAddress),
            getPersistedUserAddress(userAddress)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserAddressWithPatch() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAddress using partial update
        UserAddress partialUpdatedUserAddress = new UserAddress();
        partialUpdatedUserAddress.setId(userAddress.getId());

        partialUpdatedUserAddress.type(UPDATED_TYPE).friendlyName(UPDATED_FRIENDLY_NAME).isDefault(UPDATED_IS_DEFAULT);

        restUserAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAddress))
            )
            .andExpect(status().isOk());

        // Validate the UserAddress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAddressUpdatableFieldsEquals(partialUpdatedUserAddress, getPersistedUserAddress(partialUpdatedUserAddress));
    }

    @Test
    @Transactional
    void patchNonExistingUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userAddressDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserAddress() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userAddress
        restUserAddressMockMvc
            .perform(delete(ENTITY_API_URL_ID, userAddress.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userAddressRepository.count();
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

    protected UserAddress getPersistedUserAddress(UserAddress userAddress) {
        return userAddressRepository.findById(userAddress.getId()).orElseThrow();
    }

    protected void assertPersistedUserAddressToMatchAllProperties(UserAddress expectedUserAddress) {
        assertUserAddressAllPropertiesEquals(expectedUserAddress, getPersistedUserAddress(expectedUserAddress));
    }

    protected void assertPersistedUserAddressToMatchUpdatableProperties(UserAddress expectedUserAddress) {
        assertUserAddressAllUpdatablePropertiesEquals(expectedUserAddress, getPersistedUserAddress(expectedUserAddress));
    }
}
