package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.domain.Shipment;
import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.service.dto.OrderItemDTO;
import com.foursquare.server.service.dto.ShipmentDTO;
import com.foursquare.server.service.dto.ShipmentItemDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShipmentItem} and its DTO {@link ShipmentItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentItemMapper extends EntityMapper<ShipmentItemDTO, ShipmentItem> {
    @Mapping(target = "orderItem", source = "orderItem", qualifiedByName = "orderItemId")
    @Mapping(target = "shipment", source = "shipment", qualifiedByName = "shipmentId")
    ShipmentItemDTO toDto(ShipmentItem s);

    @Named("orderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderItemDTO toDtoOrderItemId(OrderItem orderItem);

    @Named("shipmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentDTO toDtoShipmentId(Shipment shipment);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
