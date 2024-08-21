package com.foursquare.server.service.impl;

import com.foursquare.server.domain.UserAddress;
import com.foursquare.server.repository.UserAddressRepository;
import com.foursquare.server.service.UserAddressService;
import com.foursquare.server.service.dto.UserAddressDTO;
import com.foursquare.server.service.mapper.UserAddressMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.UserAddress}.
 */
@Service
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

    private static final Logger log = LoggerFactory.getLogger(UserAddressServiceImpl.class);

    private final UserAddressRepository userAddressRepository;

    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(UserAddressRepository userAddressRepository, UserAddressMapper userAddressMapper) {
        this.userAddressRepository = userAddressRepository;
        this.userAddressMapper = userAddressMapper;
    }

    @Override
    public UserAddressDTO save(UserAddressDTO userAddressDTO) {
        log.debug("Request to save UserAddress : {}", userAddressDTO);
        UserAddress userAddress = userAddressMapper.toEntity(userAddressDTO);
        userAddress = userAddressRepository.save(userAddress);
        return userAddressMapper.toDto(userAddress);
    }

    @Override
    public UserAddressDTO update(UserAddressDTO userAddressDTO) {
        log.debug("Request to update UserAddress : {}", userAddressDTO);
        UserAddress userAddress = userAddressMapper.toEntity(userAddressDTO);
        userAddress.setIsPersisted();
        userAddress = userAddressRepository.save(userAddress);
        return userAddressMapper.toDto(userAddress);
    }

    @Override
    public Optional<UserAddressDTO> partialUpdate(UserAddressDTO userAddressDTO) {
        log.debug("Request to partially update UserAddress : {}", userAddressDTO);

        return userAddressRepository
            .findById(userAddressDTO.getId())
            .map(existingUserAddress -> {
                userAddressMapper.partialUpdate(existingUserAddress, userAddressDTO);

                return existingUserAddress;
            })
            .map(userAddressRepository::save)
            .map(userAddressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressDTO> findAll() {
        log.debug("Request to get all UserAddresses");
        return userAddressRepository.findAll().stream().map(userAddressMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<UserAddressDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userAddressRepository.findAllWithEagerRelationships(pageable).map(userAddressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAddressDTO> findOne(UUID id) {
        log.debug("Request to get UserAddress : {}", id);
        return userAddressRepository.findOneWithEagerRelationships(id).map(userAddressMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete UserAddress : {}", id);
        userAddressRepository.deleteById(id);
    }
}
