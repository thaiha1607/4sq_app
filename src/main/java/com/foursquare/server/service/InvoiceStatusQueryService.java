package com.foursquare.server.service;

import com.foursquare.server.domain.*; // for static metamodels
import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.repository.InvoiceStatusRepository;
import com.foursquare.server.service.criteria.InvoiceStatusCriteria;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
import com.foursquare.server.service.mapper.InvoiceStatusMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InvoiceStatus} entities in the database.
 * The main input is a {@link InvoiceStatusCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InvoiceStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InvoiceStatusQueryService extends QueryService<InvoiceStatus> {

    private static final Logger log = LoggerFactory.getLogger(InvoiceStatusQueryService.class);

    private final InvoiceStatusRepository invoiceStatusRepository;

    private final InvoiceStatusMapper invoiceStatusMapper;

    public InvoiceStatusQueryService(InvoiceStatusRepository invoiceStatusRepository, InvoiceStatusMapper invoiceStatusMapper) {
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.invoiceStatusMapper = invoiceStatusMapper;
    }

    /**
     * Return a {@link List} of {@link InvoiceStatusDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InvoiceStatusDTO> findByCriteria(InvoiceStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<InvoiceStatus> specification = createSpecification(criteria);
        return invoiceStatusMapper.toDto(invoiceStatusRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InvoiceStatusCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<InvoiceStatus> specification = createSpecification(criteria);
        return invoiceStatusRepository.count(specification);
    }

    /**
     * Function to convert {@link InvoiceStatusCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InvoiceStatus> createSpecification(InvoiceStatusCriteria criteria) {
        Specification<InvoiceStatus> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), InvoiceStatus_.id));
            }
            if (criteria.getStatusCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatusCode(), InvoiceStatus_.statusCode));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), InvoiceStatus_.description));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), InvoiceStatus_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), InvoiceStatus_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), InvoiceStatus_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), InvoiceStatus_.lastModifiedDate));
            }
        }
        return specification;
    }
}
