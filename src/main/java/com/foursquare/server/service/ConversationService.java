package com.foursquare.server.service;

import com.foursquare.server.domain.Conversation;
import com.foursquare.server.repository.ConversationRepository;
import com.foursquare.server.repository.search.ConversationSearchRepository;
import com.foursquare.server.service.dto.ConversationDTO;
import com.foursquare.server.service.mapper.ConversationMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.Conversation}.
 */
@Service
@Transactional
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;

    private final ConversationMapper conversationMapper;

    private final ConversationSearchRepository conversationSearchRepository;

    public ConversationService(
        ConversationRepository conversationRepository,
        ConversationMapper conversationMapper,
        ConversationSearchRepository conversationSearchRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
        this.conversationSearchRepository = conversationSearchRepository;
    }

    /**
     * Save a conversation.
     *
     * @param conversationDTO the entity to save.
     * @return the persisted entity.
     */
    public ConversationDTO save(ConversationDTO conversationDTO) {
        log.debug("Request to save Conversation : {}", conversationDTO);
        Conversation conversation = conversationMapper.toEntity(conversationDTO);
        conversation = conversationRepository.save(conversation);
        conversationSearchRepository.index(conversation);
        return conversationMapper.toDto(conversation);
    }

    /**
     * Update a conversation.
     *
     * @param conversationDTO the entity to save.
     * @return the persisted entity.
     */
    public ConversationDTO update(ConversationDTO conversationDTO) {
        log.debug("Request to update Conversation : {}", conversationDTO);
        Conversation conversation = conversationMapper.toEntity(conversationDTO);
        conversation.setIsPersisted();
        conversation = conversationRepository.save(conversation);
        conversationSearchRepository.index(conversation);
        return conversationMapper.toDto(conversation);
    }

    /**
     * Partially update a conversation.
     *
     * @param conversationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConversationDTO> partialUpdate(ConversationDTO conversationDTO) {
        log.debug("Request to partially update Conversation : {}", conversationDTO);

        return conversationRepository
            .findById(conversationDTO.getId())
            .map(existingConversation -> {
                conversationMapper.partialUpdate(existingConversation, conversationDTO);

                return existingConversation;
            })
            .map(conversationRepository::save)
            .map(savedConversation -> {
                conversationSearchRepository.index(savedConversation);
                return savedConversation;
            })
            .map(conversationMapper::toDto);
    }

    /**
     * Get all the conversations.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> findAll() {
        log.debug("Request to get all Conversations");
        return conversationRepository.findAll().stream().map(conversationMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one conversation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> findOne(UUID id) {
        log.debug("Request to get Conversation : {}", id);
        return conversationRepository.findById(id).map(conversationMapper::toDto);
    }

    /**
     * Delete the conversation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Conversation : {}", id);
        conversationRepository.deleteById(id);
        conversationSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the conversation corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> search(String query) {
        log.debug("Request to search Conversations for query {}", query);
        try {
            return StreamSupport.stream(conversationSearchRepository.search(query).spliterator(), false)
                .map(conversationMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
