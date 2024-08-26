package com.foursquare.server.repository;

import com.foursquare.server.domain.InternalOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InternalOrder entity.
 */
@Repository
public interface InternalOrderRepository extends JpaRepository<InternalOrder, UUID>, JpaSpecificationExecutor<InternalOrder> {
    default Optional<InternalOrder> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InternalOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InternalOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select internalOrder from InternalOrder internalOrder left join fetch internalOrder.status",
        countQuery = "select count(internalOrder) from InternalOrder internalOrder"
    )
    Page<InternalOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select internalOrder from InternalOrder internalOrder left join fetch internalOrder.status")
    List<InternalOrder> findAllWithToOneRelationships();

    @Query("select internalOrder from InternalOrder internalOrder left join fetch internalOrder.status where internalOrder.id =:id")
    Optional<InternalOrder> findOneWithToOneRelationships(@Param("id") UUID id);
}
