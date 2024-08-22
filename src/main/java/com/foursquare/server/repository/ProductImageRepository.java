package com.foursquare.server.repository;

import com.foursquare.server.domain.ProductImage;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID>, JpaSpecificationExecutor<ProductImage> {}
