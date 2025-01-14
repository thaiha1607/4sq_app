package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.User;
import com.foursquare.server.domain.UserDetails;
import com.foursquare.server.service.dto.UserDTO;
import com.foursquare.server.service.dto.UserDetailsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserDetails} and its DTO {@link UserDetailsDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserDetailsMapper extends EntityMapper<UserDetailsDTO, UserDetails> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    UserDetailsDTO toDto(UserDetails s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
