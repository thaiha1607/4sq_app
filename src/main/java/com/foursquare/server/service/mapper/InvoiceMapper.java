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
    @Mapping(target = "status", source = "status", qualifiedByName = "invoiceStatusStatusCode")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    @Mapping(target = "rootInvoice", source = "rootInvoice", qualifiedByName = "invoiceId")
    InvoiceDTO toDto(Invoice s);

    @Named("invoiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InvoiceDTO toDtoInvoiceId(Invoice invoice);

    @Named("invoiceStatusStatusCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "statusCode", source = "statusCode")
    InvoiceStatusDTO toDtoInvoiceStatusStatusCode(InvoiceStatus invoiceStatus);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
