package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Shipment;
import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.domain.User;
import com.foursquare.server.service.dto.ShipmentAssignmentDTO;
import com.foursquare.server.service.dto.ShipmentDTO;
import com.foursquare.server.service.dto.UserDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShipmentAssignment} and its DTO {@link ShipmentAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentAssignmentMapper extends EntityMapper<ShipmentAssignmentDTO, ShipmentAssignment> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "shipment", source = "shipment", qualifiedByName = "shipmentId")
    ShipmentAssignmentDTO toDto(ShipmentAssignment s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("shipmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentDTO toDtoShipmentId(Shipment shipment);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
