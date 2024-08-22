package com.foursquare.server.service;

import com.foursquare.server.domain.Participant;
import com.foursquare.server.repository.ParticipantRepository;
import com.foursquare.server.service.dto.ParticipantDTO;
import com.foursquare.server.service.mapper.ParticipantMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.Participant}.
 */
@Service
@Transactional
public class ParticipantService {

    private static final Logger log = LoggerFactory.getLogger(ParticipantService.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;

    public ParticipantService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Save a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    public ParticipantDTO save(ParticipantDTO participantDTO) {
        log.debug("Request to save Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    /**
     * Update a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    public ParticipantDTO update(ParticipantDTO participantDTO) {
        log.debug("Request to update Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant.setIsPersisted();
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    /**
     * Partially update a participant.
     *
     * @param participantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ParticipantDTO> partialUpdate(ParticipantDTO participantDTO) {
        log.debug("Request to partially update Participant : {}", participantDTO);

        return participantRepository
            .findById(participantDTO.getId())
            .map(existingParticipant -> {
                participantMapper.partialUpdate(existingParticipant, participantDTO);

                return existingParticipant;
            })
            .map(participantRepository::save)
            .map(participantMapper::toDto);
    }

    /**
     * Get all the participants with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ParticipantDTO> findAllWithEagerRelationships(Pageable pageable) {
        return participantRepository.findAllWithEagerRelationships(pageable).map(participantMapper::toDto);
    }

    /**
     * Get one participant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ParticipantDTO> findOne(UUID id) {
        log.debug("Request to get Participant : {}", id);
        return participantRepository.findOneWithEagerRelationships(id).map(participantMapper::toDto);
    }

    /**
     * Delete the participant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Participant : {}", id);
        participantRepository.deleteById(id);
    }
}
