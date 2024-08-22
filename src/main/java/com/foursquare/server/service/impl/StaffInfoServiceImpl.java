package com.foursquare.server.service.impl;

import com.foursquare.server.domain.StaffInfo;
import com.foursquare.server.repository.StaffInfoRepository;
import com.foursquare.server.service.StaffInfoService;
import com.foursquare.server.service.dto.StaffInfoDTO;
import com.foursquare.server.service.mapper.StaffInfoMapper;
import java.util.Optional;
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

    public StaffInfoServiceImpl(StaffInfoRepository staffInfoRepository, StaffInfoMapper staffInfoMapper) {
        this.staffInfoRepository = staffInfoRepository;
        this.staffInfoMapper = staffInfoMapper;
    }

    @Override
    public StaffInfoDTO save(StaffInfoDTO staffInfoDTO) {
        log.debug("Request to save StaffInfo : {}", staffInfoDTO);
        StaffInfo staffInfo = staffInfoMapper.toEntity(staffInfoDTO);
        staffInfo = staffInfoRepository.save(staffInfo);
        return staffInfoMapper.toDto(staffInfo);
    }

    @Override
    public StaffInfoDTO update(StaffInfoDTO staffInfoDTO) {
        log.debug("Request to update StaffInfo : {}", staffInfoDTO);
        StaffInfo staffInfo = staffInfoMapper.toEntity(staffInfoDTO);
        staffInfo.setIsPersisted();
        staffInfo = staffInfoRepository.save(staffInfo);
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
            .map(staffInfoMapper::toDto);
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
    }
}
