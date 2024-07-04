package com.foursquare.server.repository;

import com.foursquare.server.domain.Participant;
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
 * Spring Data JPA repository for the Participant entity.
 */
@Repository
@JaversSpringDataAuditable
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    @Query("select participant from Participant participant where participant.user.login = ?#{authentication.name}")
    List<Participant> findByUserIsCurrentUser();

    default Optional<Participant> findOneWithEagerRelationships(UUID id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Participant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Participant> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select participant from Participant participant left join fetch participant.user left join fetch participant.conversation",
        countQuery = "select count(participant) from Participant participant"
    )
    Page<Participant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select participant from Participant participant left join fetch participant.user left join fetch participant.conversation")
    List<Participant> findAllWithToOneRelationships();

    @Query(
        "select participant from Participant participant left join fetch participant.user left join fetch participant.conversation where participant.id =:id"
    )
    Optional<Participant> findOneWithToOneRelationships(@Param("id") UUID id);
}
