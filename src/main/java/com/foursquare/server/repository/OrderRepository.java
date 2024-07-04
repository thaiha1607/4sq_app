package com.foursquare.server.repository;

import com.foursquare.server.domain.Order;
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
 * Spring Data JPA repository for the Order entity.
 */
@Repository
@JaversSpringDataAuditable
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("select jhiOrder from Order jhiOrder where jhiOrder.creator.login = ?#{authentication.name}")
    List<Order> findByCreatorIsCurrentUser();

    @Query("select jhiOrder from Order jhiOrder where jhiOrder.customer.login = ?#{authentication.name}")
    List<Order> findByCustomerIsCurrentUser();

    default Optional<Order> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Order> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Order> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select jhiOrder from Order jhiOrder left join fetch jhiOrder.creator left join fetch jhiOrder.customer left join fetch jhiOrder.status",
        countQuery = "select count(jhiOrder) from Order jhiOrder"
    )
    Page<Order> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select jhiOrder from Order jhiOrder left join fetch jhiOrder.creator left join fetch jhiOrder.customer left join fetch jhiOrder.status"
    )
    List<Order> findAllWithToOneRelationships();

    @Query(
        "select jhiOrder from Order jhiOrder left join fetch jhiOrder.creator left join fetch jhiOrder.customer left join fetch jhiOrder.status where jhiOrder.id =:id"
    )
    Optional<Order> findOneWithToOneRelationships(@Param("id") UUID id);
}
