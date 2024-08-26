package com.foursquare.server.repository;

import com.foursquare.server.domain.InternalOrderHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InternalOrderHistory entity.
 */
@Repository
public interface InternalOrderHistoryRepository
    extends JpaRepository<InternalOrderHistory, UUID>, JpaSpecificationExecutor<InternalOrderHistory> {
    default Optional<InternalOrderHistory> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InternalOrderHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InternalOrderHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select internalOrderHistory from InternalOrderHistory internalOrderHistory left join fetch internalOrderHistory.status",
        countQuery = "select count(internalOrderHistory) from InternalOrderHistory internalOrderHistory"
    )
    Page<InternalOrderHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select internalOrderHistory from InternalOrderHistory internalOrderHistory left join fetch internalOrderHistory.status")
    List<InternalOrderHistory> findAllWithToOneRelationships();

    @Query(
        "select internalOrderHistory from InternalOrderHistory internalOrderHistory left join fetch internalOrderHistory.status where internalOrderHistory.id =:id"
    )
    Optional<InternalOrderHistory> findOneWithToOneRelationships(@Param("id") UUID id);
}
