package com.foursquare.server.service;

import com.foursquare.server.domain.Message;
import com.foursquare.server.repository.MessageRepository;
import com.foursquare.server.repository.search.MessageSearchRepository;
import com.foursquare.server.service.dto.MessageDTO;
import com.foursquare.server.service.mapper.MessageMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.Message}.
 */
@Service
@Transactional
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    private final MessageSearchRepository messageSearchRepository;

    public MessageService(
        MessageRepository messageRepository,
        MessageMapper messageMapper,
        MessageSearchRepository messageSearchRepository
    ) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.messageSearchRepository = messageSearchRepository;
    }

    /**
     * Save a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    public MessageDTO save(MessageDTO messageDTO) {
        log.debug("Request to save Message : {}", messageDTO);
        Message message = messageMapper.toEntity(messageDTO);
        message = messageRepository.save(message);
        messageSearchRepository.index(message);
        return messageMapper.toDto(message);
    }

    /**
     * Update a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    public MessageDTO update(MessageDTO messageDTO) {
        log.debug("Request to update Message : {}", messageDTO);
        Message message = messageMapper.toEntity(messageDTO);
        message.setIsPersisted();
        message = messageRepository.save(message);
        messageSearchRepository.index(message);
        return messageMapper.toDto(message);
    }

    /**
     * Partially update a message.
     *
     * @param messageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MessageDTO> partialUpdate(MessageDTO messageDTO) {
        log.debug("Request to partially update Message : {}", messageDTO);

        return messageRepository
            .findById(messageDTO.getId())
            .map(existingMessage -> {
                messageMapper.partialUpdate(existingMessage, messageDTO);

                return existingMessage;
            })
            .map(messageRepository::save)
            .map(savedMessage -> {
                messageSearchRepository.index(savedMessage);
                return savedMessage;
            })
            .map(messageMapper::toDto);
    }

    /**
     * Get all the messages.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> findAll() {
        log.debug("Request to get all Messages");
        return messageRepository.findAll().stream().map(messageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the messages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return messageRepository.findAllWithEagerRelationships(pageable).map(messageMapper::toDto);
    }

    /**
     * Get one message by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MessageDTO> findOne(UUID id) {
        log.debug("Request to get Message : {}", id);
        return messageRepository.findOneWithEagerRelationships(id).map(messageMapper::toDto);
    }

    /**
     * Delete the message by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Message : {}", id);
        messageRepository.deleteById(id);
        messageSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the message corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> search(String query) {
        log.debug("Request to search Messages for query {}", query);
        try {
            return StreamSupport.stream(messageSearchRepository.search(query).spliterator(), false).map(messageMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
