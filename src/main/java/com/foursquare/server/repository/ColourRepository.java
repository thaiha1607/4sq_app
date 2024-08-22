package com.foursquare.server.repository;

import com.foursquare.server.domain.Colour;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Colour entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ColourRepository extends JpaRepository<Colour, UUID>, JpaSpecificationExecutor<Colour> {}
