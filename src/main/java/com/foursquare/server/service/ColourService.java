package com.foursquare.server.service;

import com.foursquare.server.domain.Colour;
import com.foursquare.server.repository.ColourRepository;
import com.foursquare.server.service.dto.ColourDTO;
import com.foursquare.server.service.mapper.ColourMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.Colour}.
 */
@Service
@Transactional
public class ColourService {

    private static final Logger log = LoggerFactory.getLogger(ColourService.class);

    private final ColourRepository colourRepository;

    private final ColourMapper colourMapper;

    public ColourService(ColourRepository colourRepository, ColourMapper colourMapper) {
        this.colourRepository = colourRepository;
        this.colourMapper = colourMapper;
    }

    /**
     * Save a colour.
     *
     * @param colourDTO the entity to save.
     * @return the persisted entity.
     */
    public ColourDTO save(ColourDTO colourDTO) {
        log.debug("Request to save Colour : {}", colourDTO);
        Colour colour = colourMapper.toEntity(colourDTO);
        colour = colourRepository.save(colour);
        return colourMapper.toDto(colour);
    }

    /**
     * Update a colour.
     *
     * @param colourDTO the entity to save.
     * @return the persisted entity.
     */
    public ColourDTO update(ColourDTO colourDTO) {
        log.debug("Request to update Colour : {}", colourDTO);
        Colour colour = colourMapper.toEntity(colourDTO);
        colour.setIsPersisted();
        colour = colourRepository.save(colour);
        return colourMapper.toDto(colour);
    }

    /**
     * Partially update a colour.
     *
     * @param colourDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ColourDTO> partialUpdate(ColourDTO colourDTO) {
        log.debug("Request to partially update Colour : {}", colourDTO);

        return colourRepository
            .findById(colourDTO.getId())
            .map(existingColour -> {
                colourMapper.partialUpdate(existingColour, colourDTO);

                return existingColour;
            })
            .map(colourRepository::save)
            .map(colourMapper::toDto);
    }

    /**
     * Get all the colours.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ColourDTO> findAll() {
        log.debug("Request to get all Colours");
        return colourRepository.findAll().stream().map(colourMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one colour by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ColourDTO> findOne(UUID id) {
        log.debug("Request to get Colour : {}", id);
        return colourRepository.findById(id).map(colourMapper::toDto);
    }

    /**
     * Delete the colour by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Colour : {}", id);
        colourRepository.deleteById(id);
    }
}
