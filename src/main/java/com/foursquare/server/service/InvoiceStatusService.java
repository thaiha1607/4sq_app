package com.foursquare.server.service;

import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.repository.InvoiceStatusRepository;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
import com.foursquare.server.service.mapper.InvoiceStatusMapper;
import java.util.Optional;
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

    public InvoiceStatusService(InvoiceStatusRepository invoiceStatusRepository, InvoiceStatusMapper invoiceStatusMapper) {
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.invoiceStatusMapper = invoiceStatusMapper;
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
            .map(invoiceStatusMapper::toDto);
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
    }
}
