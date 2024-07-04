package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Address;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.UserAddress;
import com.foursquare.server.service.dto.AddressDTO;
import com.foursquare.server.service.dto.UserAddressDTO;
import com.foursquare.server.service.dto.UserDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserAddress} and its DTO {@link UserAddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserAddressMapper extends EntityMapper<UserAddressDTO, UserAddress> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    UserAddressDTO toDto(UserAddress s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
