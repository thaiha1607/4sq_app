package com.foursquare.server.service.impl;

import com.foursquare.server.domain.UserDetails;
import com.foursquare.server.repository.UserDetailsRepository;
import com.foursquare.server.repository.UserRepository;
import com.foursquare.server.repository.search.UserDetailsSearchRepository;
import com.foursquare.server.service.UserDetailsService;
import com.foursquare.server.service.dto.UserDetailsDTO;
import com.foursquare.server.service.mapper.UserDetailsMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

    private final UserDetailsSearchRepository userDetailsSearchRepository;

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(
        UserDetailsRepository userDetailsRepository,
        UserDetailsMapper userDetailsMapper,
        UserDetailsSearchRepository userDetailsSearchRepository,
        UserRepository userRepository
    ) {
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsMapper = userDetailsMapper;
        this.userDetailsSearchRepository = userDetailsSearchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetailsDTO save(UserDetailsDTO userDetailsDTO) {
        log.debug("Request to save UserDetails : {}", userDetailsDTO);
        UserDetails userDetails = userDetailsMapper.toEntity(userDetailsDTO);
        Long userId = userDetails.getUser().getId();
        userRepository.findById(userId).ifPresent(userDetails::user);
        userDetails = userDetailsRepository.save(userDetails);
        userDetailsSearchRepository.index(userDetails);
        return userDetailsMapper.toDto(userDetails);
    }

    @Override
    public UserDetailsDTO update(UserDetailsDTO userDetailsDTO) {
        log.debug("Request to update UserDetails : {}", userDetailsDTO);
        UserDetails userDetails = userDetailsMapper.toEntity(userDetailsDTO);
        userDetails.setIsPersisted();
        userDetails = userDetailsRepository.save(userDetails);
        userDetailsSearchRepository.index(userDetails);
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
            .map(savedUserDetails -> {
                userDetailsSearchRepository.index(savedUserDetails);
                return savedUserDetails;
            })
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
        userDetailsSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDTO> search(String query) {
        log.debug("Request to search UserDetails for query {}", query);
        try {
            return StreamSupport.stream(userDetailsSearchRepository.search(query).spliterator(), false)
                .map(userDetailsMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
