package com.foursquare.server.repository;

import com.foursquare.server.domain.WorkingUnit;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkingUnit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkingUnitRepository extends JpaRepository<WorkingUnit, UUID> {}
