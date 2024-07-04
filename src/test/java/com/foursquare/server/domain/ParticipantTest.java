package com.foursquare.server.domain;

import static com.foursquare.server.domain.ConversationTestSamples.*;
import static com.foursquare.server.domain.MessageTestSamples.*;
import static com.foursquare.server.domain.ParticipantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ParticipantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Participant.class);
        Participant participant1 = getParticipantSample1();
        Participant participant2 = new Participant();
        assertThat(participant1).isNotEqualTo(participant2);

        participant2.setId(participant1.getId());
        assertThat(participant1).isEqualTo(participant2);

        participant2 = getParticipantSample2();
        assertThat(participant1).isNotEqualTo(participant2);
    }

    @Test
    void conversationTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        Conversation conversationBack = getConversationRandomSampleGenerator();

        participant.setConversation(conversationBack);
        assertThat(participant.getConversation()).isEqualTo(conversationBack);

        participant.conversation(null);
        assertThat(participant.getConversation()).isNull();
    }

    @Test
    void messageTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        participant.addMessage(messageBack);
        assertThat(participant.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getParticipant()).isEqualTo(participant);

        participant.removeMessage(messageBack);
        assertThat(participant.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getParticipant()).isNull();

        participant.messages(new HashSet<>(Set.of(messageBack)));
        assertThat(participant.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getParticipant()).isEqualTo(participant);

        participant.setMessages(new HashSet<>());
        assertThat(participant.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getParticipant()).isNull();
    }
}
