package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Message;
import com.foursquare.server.domain.Participant;
import com.foursquare.server.service.dto.MessageDTO;
import com.foursquare.server.service.dto.ParticipantDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "participant", source = "participant", qualifiedByName = "participantId")
    MessageDTO toDto(Message s);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
