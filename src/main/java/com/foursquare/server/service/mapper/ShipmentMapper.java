package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Invoice;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.Shipment;
import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.service.dto.InvoiceDTO;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.dto.ShipmentDTO;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shipment} and its DTO {@link ShipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentMapper extends EntityMapper<ShipmentDTO, Shipment> {
    @Mapping(target = "status", source = "status", qualifiedByName = "shipmentStatusStatusCode")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "invoiceId")
    ShipmentDTO toDto(Shipment s);

    @Named("shipmentStatusStatusCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "statusCode", source = "statusCode")
    ShipmentStatusDTO toDtoShipmentStatusStatusCode(ShipmentStatus shipmentStatus);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("invoiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceDTO toDtoInvoiceId(Invoice invoice);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
