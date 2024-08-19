package com.foursquare.server.repository;

import com.foursquare.server.domain.ProductQuantity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductQuantity entity.
 */
@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, UUID> {
    default Optional<ProductQuantity> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductQuantity> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductQuantity> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productQuantity from ProductQuantity productQuantity left join fetch productQuantity.workingUnit left join fetch productQuantity.productCategory",
        countQuery = "select count(productQuantity) from ProductQuantity productQuantity"
    )
    Page<ProductQuantity> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select productQuantity from ProductQuantity productQuantity left join fetch productQuantity.workingUnit left join fetch productQuantity.productCategory"
    )
    List<ProductQuantity> findAllWithToOneRelationships();

    @Query(
        "select productQuantity from ProductQuantity productQuantity left join fetch productQuantity.workingUnit left join fetch productQuantity.productCategory where productQuantity.id =:id"
    )
    Optional<ProductQuantity> findOneWithToOneRelationships(@Param("id") UUID id);
}
