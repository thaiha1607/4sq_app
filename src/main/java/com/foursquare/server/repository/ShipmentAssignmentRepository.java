package com.foursquare.server.repository;

import com.foursquare.server.domain.ShipmentAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShipmentAssignment entity.
 */
@Repository
public interface ShipmentAssignmentRepository
    extends JpaRepository<ShipmentAssignment, UUID>, JpaSpecificationExecutor<ShipmentAssignment> {
    @Query(
        "select shipmentAssignment from ShipmentAssignment shipmentAssignment where shipmentAssignment.user.login = ?#{authentication.name}"
    )
    List<ShipmentAssignment> findByUserIsCurrentUser();

    default Optional<ShipmentAssignment> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ShipmentAssignment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ShipmentAssignment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select shipmentAssignment from ShipmentAssignment shipmentAssignment left join fetch shipmentAssignment.user",
        countQuery = "select count(shipmentAssignment) from ShipmentAssignment shipmentAssignment"
    )
    Page<ShipmentAssignment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select shipmentAssignment from ShipmentAssignment shipmentAssignment left join fetch shipmentAssignment.user")
    List<ShipmentAssignment> findAllWithToOneRelationships();

    @Query(
        "select shipmentAssignment from ShipmentAssignment shipmentAssignment left join fetch shipmentAssignment.user where shipmentAssignment.id =:id"
    )
    Optional<ShipmentAssignment> findOneWithToOneRelationships(@Param("id") UUID id);
}
