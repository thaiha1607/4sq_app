package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.service.dto.InvoiceStatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InvoiceStatus} and its DTO {@link InvoiceStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvoiceStatusMapper extends EntityMapper<InvoiceStatusDTO, InvoiceStatus> {}
