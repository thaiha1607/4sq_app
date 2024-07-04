package com.foursquare.server.repository;

import com.foursquare.server.domain.OrderStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderStatus entity.
 */
@SuppressWarnings("unused")
@Repository
@JaversSpringDataAuditable
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {}
