package com.foursquare.server.service;

import com.foursquare.server.domain.Participant;
import com.foursquare.server.repository.ParticipantRepository;
import com.foursquare.server.repository.search.ParticipantSearchRepository;
import com.foursquare.server.service.dto.ParticipantDTO;
import com.foursquare.server.service.mapper.ParticipantMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

    private final ParticipantSearchRepository participantSearchRepository;

    public ParticipantService(
        ParticipantRepository participantRepository,
        ParticipantMapper participantMapper,
        ParticipantSearchRepository participantSearchRepository
    ) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
        this.participantSearchRepository = participantSearchRepository;
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
        participantSearchRepository.index(participant);
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
        participantSearchRepository.index(participant);
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
            .map(savedParticipant -> {
                participantSearchRepository.index(savedParticipant);
                return savedParticipant;
            })
            .map(participantMapper::toDto);
    }

    /**
     * Get all the participants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findAll() {
        log.debug("Request to get all Participants");
        return participantRepository.findAll().stream().map(participantMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
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
        participantSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the participant corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> search(String query) {
        log.debug("Request to search Participants for query {}", query);
        try {
            return StreamSupport.stream(participantSearchRepository.search(query).spliterator(), false)
                .map(participantMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
