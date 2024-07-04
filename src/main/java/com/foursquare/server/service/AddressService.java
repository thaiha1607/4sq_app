package com.foursquare.server.service;

import com.foursquare.server.domain.Address;
import com.foursquare.server.repository.AddressRepository;
import com.foursquare.server.repository.search.AddressSearchRepository;
import com.foursquare.server.service.dto.AddressDTO;
import com.foursquare.server.service.mapper.AddressMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.Address}.
 */
@Service
@Transactional
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    private final AddressSearchRepository addressSearchRepository;

    public AddressService(
        AddressRepository addressRepository,
        AddressMapper addressMapper,
        AddressSearchRepository addressSearchRepository
    ) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.addressSearchRepository = addressSearchRepository;
    }

    /**
     * Save a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public AddressDTO save(AddressDTO addressDTO) {
        log.debug("Request to save Address : {}", addressDTO);
        Address address = addressMapper.toEntity(addressDTO);
        address = addressRepository.save(address);
        addressSearchRepository.index(address);
        return addressMapper.toDto(address);
    }

    /**
     * Update a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public AddressDTO update(AddressDTO addressDTO) {
        log.debug("Request to update Address : {}", addressDTO);
        Address address = addressMapper.toEntity(addressDTO);
        address.setIsPersisted();
        address = addressRepository.save(address);
        addressSearchRepository.index(address);
        return addressMapper.toDto(address);
    }

    /**
     * Partially update a address.
     *
     * @param addressDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AddressDTO> partialUpdate(AddressDTO addressDTO) {
        log.debug("Request to partially update Address : {}", addressDTO);

        return addressRepository
            .findById(addressDTO.getId())
            .map(existingAddress -> {
                addressMapper.partialUpdate(existingAddress, addressDTO);

                return existingAddress;
            })
            .map(addressRepository::save)
            .map(savedAddress -> {
                addressSearchRepository.index(savedAddress);
                return savedAddress;
            })
            .map(addressMapper::toDto);
    }

    /**
     * Get all the addresses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> findAll() {
        log.debug("Request to get all Addresses");
        return addressRepository.findAll().stream().map(addressMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one address by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AddressDTO> findOne(UUID id) {
        log.debug("Request to get Address : {}", id);
        return addressRepository.findById(id).map(addressMapper::toDto);
    }

    /**
     * Delete the address by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Address : {}", id);
        addressRepository.deleteById(id);
        addressSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the address corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> search(String query) {
        log.debug("Request to search Addresses for query {}", query);
        try {
            return StreamSupport.stream(addressSearchRepository.search(query).spliterator(), false).map(addressMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
