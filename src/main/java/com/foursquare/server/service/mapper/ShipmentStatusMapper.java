package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.service.dto.ShipmentStatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShipmentStatus} and its DTO {@link ShipmentStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentStatusMapper extends EntityMapper<ShipmentStatusDTO, ShipmentStatus> {}
