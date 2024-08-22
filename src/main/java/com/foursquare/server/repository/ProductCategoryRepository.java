package com.foursquare.server.repository;

import com.foursquare.server.domain.ProductCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductCategory entity.
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID>, JpaSpecificationExecutor<ProductCategory> {
    default Optional<ProductCategory> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductCategory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductCategory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productCategory from ProductCategory productCategory left join fetch productCategory.colour left join fetch productCategory.product",
        countQuery = "select count(productCategory) from ProductCategory productCategory"
    )
    Page<ProductCategory> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select productCategory from ProductCategory productCategory left join fetch productCategory.colour left join fetch productCategory.product"
    )
    List<ProductCategory> findAllWithToOneRelationships();

    @Query(
        "select productCategory from ProductCategory productCategory left join fetch productCategory.colour left join fetch productCategory.product where productCategory.id =:id"
    )
    Optional<ProductCategory> findOneWithToOneRelationships(@Param("id") UUID id);
}
