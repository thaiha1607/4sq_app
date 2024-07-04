package com.foursquare.server.domain;

import static com.foursquare.server.domain.MessageTestSamples.*;
import static com.foursquare.server.domain.ParticipantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Message.class);
        Message message1 = getMessageSample1();
        Message message2 = new Message();
        assertThat(message1).isNotEqualTo(message2);

        message2.setId(message1.getId());
        assertThat(message1).isEqualTo(message2);

        message2 = getMessageSample2();
        assertThat(message1).isNotEqualTo(message2);
    }

    @Test
    void participantTest() {
        Message message = getMessageRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        message.setParticipant(participantBack);
        assertThat(message.getParticipant()).isEqualTo(participantBack);

        message.participant(null);
        assertThat(message.getParticipant()).isNull();
    }
}