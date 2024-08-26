package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.InternalOrder;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.service.dto.InternalOrderDTO;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.dto.OrderStatusDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InternalOrder} and its DTO {@link InternalOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface InternalOrderMapper extends EntityMapper<InternalOrderDTO, InternalOrder> {
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusStatusCode")
    @Mapping(target = "rootOrder", source = "rootOrder", qualifiedByName = "orderId")
    InternalOrderDTO toDto(InternalOrder s);

    @Named("orderStatusStatusCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "statusCode", source = "statusCode")
    OrderStatusDTO toDtoOrderStatusStatusCode(OrderStatus orderStatus);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
