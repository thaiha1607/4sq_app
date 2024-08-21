package com.foursquare.server.service.impl;

import com.foursquare.server.domain.UserDetails;
import com.foursquare.server.repository.UserDetailsRepository;
import com.foursquare.server.service.UserDetailsService;
import com.foursquare.server.service.dto.UserDetailsDTO;
import com.foursquare.server.service.mapper.UserDetailsMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.UserDetails}.
 */
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserDetailsRepository userDetailsRepository;

    private final UserDetailsMapper userDetailsMapper;

    public UserDetailsServiceImpl(UserDetailsRepository userDetailsRepository, UserDetailsMapper userDetailsMapper) {
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsMapper = userDetailsMapper;
    }

    @Override
    public UserDetailsDTO save(UserDetailsDTO userDetailsDTO) {
        log.debug("Request to save UserDetails : {}", userDetailsDTO);
        UserDetails userDetails = userDetailsMapper.toEntity(userDetailsDTO);
        userDetails = userDetailsRepository.save(userDetails);
        return userDetailsMapper.toDto(userDetails);
    }

    @Override
    public UserDetailsDTO update(UserDetailsDTO userDetailsDTO) {
        log.debug("Request to update UserDetails : {}", userDetailsDTO);
        UserDetails userDetails = userDetailsMapper.toEntity(userDetailsDTO);
        userDetails.setIsPersisted();
        userDetails = userDetailsRepository.save(userDetails);
        return userDetailsMapper.toDto(userDetails);
    }

    @Override
    public Optional<UserDetailsDTO> partialUpdate(UserDetailsDTO userDetailsDTO) {
        log.debug("Request to partially update UserDetails : {}", userDetailsDTO);

        return userDetailsRepository
            .findById(userDetailsDTO.getId())
            .map(existingUserDetails -> {
                userDetailsMapper.partialUpdate(existingUserDetails, userDetailsDTO);

                return existingUserDetails;
            })
            .map(userDetailsRepository::save)
            .map(userDetailsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDTO> findAll() {
        log.debug("Request to get all UserDetails");
        return userDetailsRepository.findAll().stream().map(userDetailsMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<UserDetailsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userDetailsRepository.findAllWithEagerRelationships(pageable).map(userDetailsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDetailsDTO> findOne(Long id) {
        log.debug("Request to get UserDetails : {}", id);
        return userDetailsRepository.findOneWithEagerRelationships(id).map(userDetailsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserDetails : {}", id);
        userDetailsRepository.deleteById(id);
    }
}
