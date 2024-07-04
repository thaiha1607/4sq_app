package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Conversation;
import com.foursquare.server.domain.Participant;
import com.foursquare.server.domain.User;
import com.foursquare.server.service.dto.ConversationDTO;
import com.foursquare.server.service.dto.ParticipantDTO;
import com.foursquare.server.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "conversation", source = "conversation", qualifiedByName = "conversationTitle")
    ParticipantDTO toDto(Participant s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("conversationTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ConversationDTO toDtoConversationTitle(Conversation conversation);
}
