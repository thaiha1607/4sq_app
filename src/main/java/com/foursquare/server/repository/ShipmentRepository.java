package com.foursquare.server.repository;

import com.foursquare.server.domain.Shipment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Shipment entity.
 */
@Repository
@JaversSpringDataAuditable
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    default Optional<Shipment> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Shipment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Shipment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select shipment from Shipment shipment left join fetch shipment.status",
        countQuery = "select count(shipment) from Shipment shipment"
    )
    Page<Shipment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select shipment from Shipment shipment left join fetch shipment.status")
    List<Shipment> findAllWithToOneRelationships();

    @Query("select shipment from Shipment shipment left join fetch shipment.status where shipment.id =:id")
    Optional<Shipment> findOneWithToOneRelationships(@Param("id") UUID id);
}
