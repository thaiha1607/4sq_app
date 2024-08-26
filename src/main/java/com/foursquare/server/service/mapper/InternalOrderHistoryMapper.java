package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.InternalOrder;
import com.foursquare.server.domain.InternalOrderHistory;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.service.dto.InternalOrderDTO;
import com.foursquare.server.service.dto.InternalOrderHistoryDTO;
import com.foursquare.server.service.dto.OrderStatusDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InternalOrderHistory} and its DTO {@link InternalOrderHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface InternalOrderHistoryMapper extends EntityMapper<InternalOrderHistoryDTO, InternalOrderHistory> {
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusStatusCode")
    @Mapping(target = "order", source = "order", qualifiedByName = "internalOrderId")
    InternalOrderHistoryDTO toDto(InternalOrderHistory s);

    @Named("orderStatusStatusCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "statusCode", source = "statusCode")
    OrderStatusDTO toDtoOrderStatusStatusCode(OrderStatus orderStatus);

    @Named("internalOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InternalOrderDTO toDtoInternalOrderId(InternalOrder internalOrder);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
