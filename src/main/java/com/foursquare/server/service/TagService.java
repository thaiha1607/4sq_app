package com.foursquare.server.service;

import com.foursquare.server.domain.Tag;
import com.foursquare.server.repository.TagRepository;
import com.foursquare.server.repository.search.TagSearchRepository;
import com.foursquare.server.service.dto.TagDTO;
import com.foursquare.server.service.mapper.TagMapper;
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
 * Service Implementation for managing {@link com.foursquare.server.domain.Tag}.
 */
@Service
@Transactional
public class TagService {

    private static final Logger log = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    private final TagSearchRepository tagSearchRepository;

    public TagService(TagRepository tagRepository, TagMapper tagMapper, TagSearchRepository tagSearchRepository) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.tagSearchRepository = tagSearchRepository;
    }

    /**
     * Save a tag.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    public TagDTO save(TagDTO tagDTO) {
        log.debug("Request to save Tag : {}", tagDTO);
        Tag tag = tagMapper.toEntity(tagDTO);
        tag = tagRepository.save(tag);
        tagSearchRepository.index(tag);
        return tagMapper.toDto(tag);
    }

    /**
     * Update a tag.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    public TagDTO update(TagDTO tagDTO) {
        log.debug("Request to update Tag : {}", tagDTO);
        Tag tag = tagMapper.toEntity(tagDTO);
        tag.setIsPersisted();
        tag = tagRepository.save(tag);
        tagSearchRepository.index(tag);
        return tagMapper.toDto(tag);
    }

    /**
     * Partially update a tag.
     *
     * @param tagDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TagDTO> partialUpdate(TagDTO tagDTO) {
        log.debug("Request to partially update Tag : {}", tagDTO);

        return tagRepository
            .findById(tagDTO.getId())
            .map(existingTag -> {
                tagMapper.partialUpdate(existingTag, tagDTO);

                return existingTag;
            })
            .map(tagRepository::save)
            .map(savedTag -> {
                tagSearchRepository.index(savedTag);
                return savedTag;
            })
            .map(tagMapper::toDto);
    }

    /**
     * Get all the tags.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TagDTO> findAll() {
        log.debug("Request to get all Tags");
        return tagRepository.findAll().stream().map(tagMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one tag by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TagDTO> findOne(UUID id) {
        log.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id).map(tagMapper::toDto);
    }

    /**
     * Delete the tag by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Tag : {}", id);
        tagRepository.deleteById(id);
        tagSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the tag corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TagDTO> search(String query) {
        log.debug("Request to search Tags for query {}", query);
        try {
            return StreamSupport.stream(tagSearchRepository.search(query).spliterator(), false).map(tagMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
