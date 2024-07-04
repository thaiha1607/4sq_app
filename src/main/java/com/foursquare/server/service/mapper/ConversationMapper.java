package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Conversation;
import com.foursquare.server.service.dto.ConversationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Conversation} and its DTO {@link ConversationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConversationMapper extends EntityMapper<ConversationDTO, Conversation> {}
