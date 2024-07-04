package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.WarehouseAssignment;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.dto.UserDTO;
import com.foursquare.server.service.dto.WarehouseAssignmentDTO;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WarehouseAssignment} and its DTO {@link WarehouseAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseAssignmentMapper extends EntityMapper<WarehouseAssignmentDTO, WarehouseAssignment> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "sourceWorkingUnit", source = "sourceWorkingUnit", qualifiedByName = "workingUnitName")
    @Mapping(target = "targetWorkingUnit", source = "targetWorkingUnit", qualifiedByName = "workingUnitName")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    WarehouseAssignmentDTO toDto(WarehouseAssignment s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("workingUnitName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WorkingUnitDTO toDtoWorkingUnitName(WorkingUnit workingUnit);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
