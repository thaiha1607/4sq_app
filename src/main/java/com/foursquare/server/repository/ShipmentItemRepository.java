package com.foursquare.server.repository;

import com.foursquare.server.domain.ShipmentItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShipmentItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, UUID>, JpaSpecificationExecutor<ShipmentItem> {}
