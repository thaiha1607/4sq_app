package com.foursquare.server.repository;

import com.foursquare.server.domain.InvoiceStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InvoiceStatus entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long> {}
