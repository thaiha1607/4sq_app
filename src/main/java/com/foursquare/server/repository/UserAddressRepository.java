package com.foursquare.server.repository;

import com.foursquare.server.domain.UserAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserAddress entity.
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    @Query("select userAddress from UserAddress userAddress where userAddress.user.login = ?#{authentication.name}")
    List<UserAddress> findByUserIsCurrentUser();

    default Optional<UserAddress> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserAddress> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserAddress> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select userAddress from UserAddress userAddress left join fetch userAddress.user",
        countQuery = "select count(userAddress) from UserAddress userAddress"
    )
    Page<UserAddress> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userAddress from UserAddress userAddress left join fetch userAddress.user")
    List<UserAddress> findAllWithToOneRelationships();

    @Query("select userAddress from UserAddress userAddress left join fetch userAddress.user where userAddress.id =:id")
    Optional<UserAddress> findOneWithToOneRelationships(@Param("id") UUID id);
}
