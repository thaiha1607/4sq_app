package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.InternalOrderItem;
import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.service.dto.InternalOrderItemDTO;
import com.foursquare.server.service.dto.OrderItemDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InternalOrderItem} and its DTO {@link InternalOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface InternalOrderItemMapper extends EntityMapper<InternalOrderItemDTO, InternalOrderItem> {
    @Mapping(target = "orderItem", source = "orderItem", qualifiedByName = "orderItemId")
    InternalOrderItemDTO toDto(InternalOrderItem s);

    @Named("orderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderItemDTO toDtoOrderItemId(OrderItem orderItem);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
