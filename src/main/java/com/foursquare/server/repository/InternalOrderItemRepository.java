package com.foursquare.server.repository;

import com.foursquare.server.domain.InternalOrderItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InternalOrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InternalOrderItemRepository extends JpaRepository<InternalOrderItem, UUID>, JpaSpecificationExecutor<InternalOrderItem> {}
