package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.StaffInfo;
import com.foursquare.server.domain.User;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.service.dto.StaffInfoDTO;
import com.foursquare.server.service.dto.UserDTO;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StaffInfo} and its DTO {@link StaffInfoDTO}.
 */
@Mapper(componentModel = "spring")
public interface StaffInfoMapper extends EntityMapper<StaffInfoDTO, StaffInfo> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "workingUnit", source = "workingUnit", qualifiedByName = "workingUnitName")
    StaffInfoDTO toDto(StaffInfo s);

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
}
