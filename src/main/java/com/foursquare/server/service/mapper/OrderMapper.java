package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Address;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.domain.User;
import com.foursquare.server.service.dto.AddressDTO;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.dto.OrderStatusDTO;
import com.foursquare.server.service.dto.UserDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "userLogin")
    @Mapping(target = "status", source = "status", qualifiedByName = "orderStatusDescription")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    @Mapping(target = "parentOrder", source = "parentOrder", qualifiedByName = "orderId")
    OrderDTO toDto(Order s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("orderStatusDescription")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "statusCode", source = "statusCode")
    @Mapping(target = "description", source = "description")
    OrderStatusDTO toDtoOrderStatusDescription(OrderStatus orderStatus);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
