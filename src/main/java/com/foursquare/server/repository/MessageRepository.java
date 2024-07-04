package com.foursquare.server.repository;

import com.foursquare.server.domain.Message;
import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface MessageRepository extends JpaRepository<Message, UUID> {}
