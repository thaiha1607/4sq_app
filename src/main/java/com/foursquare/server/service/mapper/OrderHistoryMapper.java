package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderHistory;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.dto.OrderHistoryDTO;
import com.foursquare.server.service.dto.OrderStatusDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderHistory} and its DTO {@link OrderHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderHistoryMapper extends EntityMapper<OrderHistoryDTO, OrderHistory> {
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusStatusCode")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    OrderHistoryDTO toDto(OrderHistory s);

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
