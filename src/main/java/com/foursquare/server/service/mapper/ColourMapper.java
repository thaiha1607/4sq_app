package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Colour;
import com.foursquare.server.service.dto.ColourDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Colour} and its DTO {@link ColourDTO}.
 */
@Mapper(componentModel = "spring")
public interface ColourMapper extends EntityMapper<ColourDTO, Colour> {}
