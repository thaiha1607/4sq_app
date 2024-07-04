package com.foursquare.server.domain;

import static com.foursquare.server.domain.ConversationTestSamples.*;
import static com.foursquare.server.domain.ParticipantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConversationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Conversation.class);
        Conversation conversation1 = getConversationSample1();
        Conversation conversation2 = new Conversation();
        assertThat(conversation1).isNotEqualTo(conversation2);

        conversation2.setId(conversation1.getId());
        assertThat(conversation1).isEqualTo(conversation2);

        conversation2 = getConversationSample2();
        assertThat(conversation1).isNotEqualTo(conversation2);
    }

    @Test
    void participantTest() {
        Conversation conversation = getConversationRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        conversation.addParticipant(participantBack);
        assertThat(conversation.getParticipants()).containsOnly(participantBack);
        assertThat(participantBack.getConversation()).isEqualTo(conversation);

        conversation.removeParticipant(participantBack);
        assertThat(conversation.getParticipants()).doesNotContain(participantBack);
        assertThat(participantBack.getConversation()).isNull();

        conversation.participants(new HashSet<>(Set.of(participantBack)));
        assertThat(conversation.getParticipants()).containsOnly(participantBack);
        assertThat(participantBack.getConversation()).isEqualTo(conversation);

        conversation.setParticipants(new HashSet<>());
        assertThat(conversation.getParticipants()).doesNotContain(participantBack);
        assertThat(participantBack.getConversation()).isNull();
    }
}
