package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Invoice;
import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.domain.Order;
import com.foursquare.server.service.dto.InvoiceDTO;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
import com.foursquare.server.service.dto.OrderDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invoice} and its DTO {@link InvoiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends EntityMapper<InvoiceDTO, Invoice> {
    @Mapping(target = "status", source = "status", qualifiedByName = "invoiceStatusDescription")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    InvoiceDTO toDto(Invoice s);

    @Named("invoiceStatusDescription")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "statusCode", source = "statusCode")
    @Mapping(target = "description", source = "description")
    InvoiceStatusDTO toDtoInvoiceStatusDescription(InvoiceStatus invoiceStatus);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
