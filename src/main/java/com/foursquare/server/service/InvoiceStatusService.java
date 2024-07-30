package com.foursquare.server.service;

import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.repository.InvoiceStatusRepository;
import com.foursquare.server.repository.search.InvoiceStatusSearchRepository;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
import com.foursquare.server.service.mapper.InvoiceStatusMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.InvoiceStatus}.
 */
@Service
@Transactional
public class InvoiceStatusService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceStatusService.class);

    private final InvoiceStatusRepository invoiceStatusRepository;

    private final InvoiceStatusMapper invoiceStatusMapper;

    private final InvoiceStatusSearchRepository invoiceStatusSearchRepository;

    public InvoiceStatusService(
        InvoiceStatusRepository invoiceStatusRepository,
        InvoiceStatusMapper invoiceStatusMapper,
        InvoiceStatusSearchRepository invoiceStatusSearchRepository
    ) {
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.invoiceStatusMapper = invoiceStatusMapper;
        this.invoiceStatusSearchRepository = invoiceStatusSearchRepository;
    }

    /**
     * Save a invoiceStatus.
     *
     * @param invoiceStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public InvoiceStatusDTO save(InvoiceStatusDTO invoiceStatusDTO) {
        log.debug("Request to save InvoiceStatus : {}", invoiceStatusDTO);
        InvoiceStatus invoiceStatus = invoiceStatusMapper.toEntity(invoiceStatusDTO);
        invoiceStatus = invoiceStatusRepository.save(invoiceStatus);
        invoiceStatusSearchRepository.index(invoiceStatus);
        return invoiceStatusMapper.toDto(invoiceStatus);
    }

    /**
     * Update a invoiceStatus.
     *
     * @param invoiceStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public InvoiceStatusDTO update(InvoiceStatusDTO invoiceStatusDTO) {
        log.debug("Request to update InvoiceStatus : {}", invoiceStatusDTO);
        InvoiceStatus invoiceStatus = invoiceStatusMapper.toEntity(invoiceStatusDTO);
        invoiceStatus.setIsPersisted();
        invoiceStatus = invoiceStatusRepository.save(invoiceStatus);
        invoiceStatusSearchRepository.index(invoiceStatus);
        return invoiceStatusMapper.toDto(invoiceStatus);
    }

    /**
     * Partially update a invoiceStatus.
     *
     * @param invoiceStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InvoiceStatusDTO> partialUpdate(InvoiceStatusDTO invoiceStatusDTO) {
        log.debug("Request to partially update InvoiceStatus : {}", invoiceStatusDTO);

        return invoiceStatusRepository
            .findById(invoiceStatusDTO.getId())
            .map(existingInvoiceStatus -> {
                invoiceStatusMapper.partialUpdate(existingInvoiceStatus, invoiceStatusDTO);

                return existingInvoiceStatus;
            })
            .map(invoiceStatusRepository::save)
            .map(savedInvoiceStatus -> {
                invoiceStatusSearchRepository.index(savedInvoiceStatus);
                return savedInvoiceStatus;
            })
            .map(invoiceStatusMapper::toDto);
    }

    /**
     * Get all the invoiceStatuses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceStatusDTO> findAll() {
        log.debug("Request to get all InvoiceStatuses");
        return invoiceStatusRepository.findAll().stream().map(invoiceStatusMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one invoiceStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InvoiceStatusDTO> findOne(Long id) {
        log.debug("Request to get InvoiceStatus : {}", id);
        return invoiceStatusRepository.findById(id).map(invoiceStatusMapper::toDto);
    }

    /**
     * Delete the invoiceStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete InvoiceStatus : {}", id);
        invoiceStatusRepository.deleteById(id);
        invoiceStatusSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the invoiceStatus corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceStatusDTO> search(String query) {
        log.debug("Request to search InvoiceStatuses for query {}", query);
        try {
            return StreamSupport.stream(invoiceStatusSearchRepository.search(query).spliterator(), false)
                .map(invoiceStatusMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
