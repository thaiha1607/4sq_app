package com.foursquare.server.repository;

import com.foursquare.server.domain.ShipmentStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShipmentStatus entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Long> {}
