package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Conversation;
import com.foursquare.server.domain.Message;
import com.foursquare.server.domain.Participant;
import com.foursquare.server.domain.User;
import com.foursquare.server.service.dto.ConversationDTO;
import com.foursquare.server.service.dto.MessageDTO;
import com.foursquare.server.service.dto.ParticipantDTO;
import com.foursquare.server.service.dto.UserDTO;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "conversation", source = "conversation", qualifiedByName = "conversationTitle")
    @Mapping(target = "seenMessages", source = "seenMessages", qualifiedByName = "messageIdSet")
    ParticipantDTO toDto(Participant s);

    @Mapping(target = "seenMessages", ignore = true)
    @Mapping(target = "removeSeenMessage", ignore = true)
    Participant toEntity(ParticipantDTO participantDTO);

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

    @Named("messageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MessageDTO toDtoMessageId(Message message);

    @Named("messageIdSet")
    default Set<MessageDTO> toDtoMessageIdSet(Set<Message> message) {
        return message.stream().map(this::toDtoMessageId).collect(Collectors.toSet());
    }

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
