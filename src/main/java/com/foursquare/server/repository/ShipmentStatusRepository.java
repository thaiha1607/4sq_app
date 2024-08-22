package com.foursquare.server.repository;

import com.foursquare.server.domain.ShipmentStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShipmentStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Long>, JpaSpecificationExecutor<ShipmentStatus> {}
