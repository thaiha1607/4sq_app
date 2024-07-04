package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.service.dto.OrderStatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderStatus} and its DTO {@link OrderStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderStatusMapper extends EntityMapper<OrderStatusDTO, OrderStatus> {}
