package com.foursquare.server.repository;

import com.foursquare.server.domain.StaffInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StaffInfo entity.
 */
@Repository
public interface StaffInfoRepository extends JpaRepository<StaffInfo, Long>, JpaSpecificationExecutor<StaffInfo> {
    default Optional<StaffInfo> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StaffInfo> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StaffInfo> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select staffInfo from StaffInfo staffInfo left join fetch staffInfo.user left join fetch staffInfo.workingUnit",
        countQuery = "select count(staffInfo) from StaffInfo staffInfo"
    )
    Page<StaffInfo> findAllWithToOneRelationships(Pageable pageable);

    @Query("select staffInfo from StaffInfo staffInfo left join fetch staffInfo.user left join fetch staffInfo.workingUnit")
    List<StaffInfo> findAllWithToOneRelationships();

    @Query(
        "select staffInfo from StaffInfo staffInfo left join fetch staffInfo.user left join fetch staffInfo.workingUnit where staffInfo.id =:id"
    )
    Optional<StaffInfo> findOneWithToOneRelationships(@Param("id") Long id);
}
