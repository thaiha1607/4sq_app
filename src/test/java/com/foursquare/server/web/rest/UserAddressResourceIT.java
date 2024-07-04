package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.UserAddressAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
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
import com.foursquare.server.repository.search.UserAddressSearchRepository;
import com.foursquare.server.service.UserAddressService;
import com.foursquare.server.service.dto.UserAddressDTO;
import com.foursquare.server.service.mapper.UserAddressMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
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
import org.springframework.data.util.Streamable;
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
    private static final String ENTITY_SEARCH_API_URL = "/api/user-addresses/_search";

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
    private UserAddressSearchRepository userAddressSearchRepository;

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
            userAddressSearchRepository.delete(insertedUserAddress);
            insertedUserAddress = null;
        }
    }

    @Test
    @Transactional
    void createUserAddress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
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

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserAddress = returnedUserAddress;
    }

    @Test
    @Transactional
    void createUserAddressWithExistingId() throws Exception {
        // Create the UserAddress with an existing ID
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAddressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        // set the field null
        userAddress.setType(null);

        // Create the UserAddress, which fails.
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        restUserAddressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
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
        userAddressSearchRepository.save(userAddress);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());

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

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserAddress> userAddressSearchList = Streamable.of(userAddressSearchRepository.findAll()).toList();
                UserAddress testUserAddressSearch = userAddressSearchList.get(searchDatabaseSizeAfter - 1);

                assertUserAddressAllPropertiesEquals(testUserAddressSearch, updatedUserAddress);
            });
    }

    @Test
    @Transactional
    void putNonExistingUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
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
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
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
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        userAddress.setId(UUID.randomUUID());

        // Create the UserAddress
        UserAddressDTO userAddressDTO = userAddressMapper.toDto(userAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAddressMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userAddressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAddress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUserAddress() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);
        userAddressRepository.save(userAddress);
        userAddressSearchRepository.save(userAddress);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userAddress
        restUserAddressMockMvc
            .perform(delete(ENTITY_API_URL_ID, userAddress.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userAddressSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUserAddress() throws Exception {
        // Initialize the database
        insertedUserAddress = userAddressRepository.saveAndFlush(userAddress);
        userAddressSearchRepository.save(userAddress);

        // Search the userAddress
        restUserAddressMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAddress.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].friendlyName").value(hasItem(DEFAULT_FRIENDLY_NAME)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())));
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
