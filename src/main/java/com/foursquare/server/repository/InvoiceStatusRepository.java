package com.foursquare.server.repository;

import com.foursquare.server.domain.InvoiceStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InvoiceStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long>, JpaSpecificationExecutor<InvoiceStatus> {}
