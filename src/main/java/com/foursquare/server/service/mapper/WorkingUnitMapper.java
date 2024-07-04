package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Address;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.service.dto.AddressDTO;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkingUnit} and its DTO {@link WorkingUnitDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkingUnitMapper extends EntityMapper<WorkingUnitDTO, WorkingUnit> {
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    WorkingUnitDTO toDto(WorkingUnit s);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
