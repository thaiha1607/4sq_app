package com.foursquare.server.repository;

import com.foursquare.server.domain.WarehouseAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WarehouseAssignment entity.
 */
@Repository
public interface WarehouseAssignmentRepository extends JpaRepository<WarehouseAssignment, UUID> {
    @Query(
        "select warehouseAssignment from WarehouseAssignment warehouseAssignment where warehouseAssignment.user.login = ?#{authentication.name}"
    )
    List<WarehouseAssignment> findByUserIsCurrentUser();

    default Optional<WarehouseAssignment> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WarehouseAssignment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WarehouseAssignment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select warehouseAssignment from WarehouseAssignment warehouseAssignment left join fetch warehouseAssignment.user left join fetch warehouseAssignment.sourceWorkingUnit left join fetch warehouseAssignment.targetWorkingUnit",
        countQuery = "select count(warehouseAssignment) from WarehouseAssignment warehouseAssignment"
    )
    Page<WarehouseAssignment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select warehouseAssignment from WarehouseAssignment warehouseAssignment left join fetch warehouseAssignment.user left join fetch warehouseAssignment.sourceWorkingUnit left join fetch warehouseAssignment.targetWorkingUnit"
    )
    List<WarehouseAssignment> findAllWithToOneRelationships();

    @Query(
        "select warehouseAssignment from WarehouseAssignment warehouseAssignment left join fetch warehouseAssignment.user left join fetch warehouseAssignment.sourceWorkingUnit left join fetch warehouseAssignment.targetWorkingUnit where warehouseAssignment.id =:id"
    )
    Optional<WarehouseAssignment> findOneWithToOneRelationships(@Param("id") UUID id);
}
