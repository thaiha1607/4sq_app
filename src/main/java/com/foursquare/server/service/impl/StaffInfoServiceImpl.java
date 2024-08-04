package com.foursquare.server.service.impl;

import com.foursquare.server.domain.StaffInfo;
import com.foursquare.server.repository.StaffInfoRepository;
import com.foursquare.server.repository.search.StaffInfoSearchRepository;
import com.foursquare.server.service.StaffInfoService;
import com.foursquare.server.service.dto.StaffInfoDTO;
import com.foursquare.server.service.mapper.StaffInfoMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.StaffInfo}.
 */
@Service
@Transactional
public class StaffInfoServiceImpl implements StaffInfoService {

    private static final Logger log = LoggerFactory.getLogger(StaffInfoServiceImpl.class);

    private final StaffInfoRepository staffInfoRepository;

    private final StaffInfoMapper staffInfoMapper;

    private final StaffInfoSearchRepository staffInfoSearchRepository;

    public StaffInfoServiceImpl(
        StaffInfoRepository staffInfoRepository,
        StaffInfoMapper staffInfoMapper,
        StaffInfoSearchRepository staffInfoSearchRepository
    ) {
        this.staffInfoRepository = staffInfoRepository;
        this.staffInfoMapper = staffInfoMapper;
        this.staffInfoSearchRepository = staffInfoSearchRepository;
    }

    @Override
    public StaffInfoDTO save(StaffInfoDTO staffInfoDTO) {
        log.debug("Request to save StaffInfo : {}", staffInfoDTO);
        StaffInfo staffInfo = staffInfoMapper.toEntity(staffInfoDTO);
        staffInfo = staffInfoRepository.save(staffInfo);
        staffInfoSearchRepository.index(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }

    @Override
    public StaffInfoDTO update(StaffInfoDTO staffInfoDTO) {
        log.debug("Request to update StaffInfo : {}", staffInfoDTO);
        StaffInfo staffInfo = staffInfoMapper.toEntity(staffInfoDTO);
        staffInfo.setIsPersisted();
        staffInfo = staffInfoRepository.save(staffInfo);
        staffInfoSearchRepository.index(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }

    @Override
    public Optional<StaffInfoDTO> partialUpdate(StaffInfoDTO staffInfoDTO) {
        log.debug("Request to partially update StaffInfo : {}", staffInfoDTO);

        return staffInfoRepository
            .findById(staffInfoDTO.getId())
            .map(existingStaffInfo -> {
                staffInfoMapper.partialUpdate(existingStaffInfo, staffInfoDTO);

                return existingStaffInfo;
            })
            .map(staffInfoRepository::save)
            .map(savedStaffInfo -> {
                staffInfoSearchRepository.index(savedStaffInfo);
                return savedStaffInfo;
            })
            .map(staffInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffInfoDTO> findAll() {
        log.debug("Request to get all StaffInfos");
        return staffInfoRepository.findAll().stream().map(staffInfoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<StaffInfoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return staffInfoRepository.findAllWithEagerRelationships(pageable).map(staffInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffInfoDTO> findOne(Long id) {
        log.debug("Request to get StaffInfo : {}", id);
        return staffInfoRepository.findOneWithEagerRelationships(id).map(staffInfoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete StaffInfo : {}", id);
        staffInfoRepository.deleteById(id);
        staffInfoSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffInfoDTO> search(String query) {
        log.debug("Request to search StaffInfos for query {}", query);
        try {
            return StreamSupport.stream(staffInfoSearchRepository.search(query).spliterator(), false).map(staffInfoMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
