package com.foursquare.server.repository;

import com.foursquare.server.domain.Tag;
import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface TagRepository extends JpaRepository<Tag, UUID> {}
