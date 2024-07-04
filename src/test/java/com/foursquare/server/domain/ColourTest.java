package com.foursquare.server.domain;

import static com.foursquare.server.domain.ColourTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ColourTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Colour.class);
        Colour colour1 = getColourSample1();
        Colour colour2 = new Colour();
        assertThat(colour1).isNotEqualTo(colour2);

        colour2.setId(colour1.getId());
        assertThat(colour1).isEqualTo(colour2);

        colour2 = getColourSample2();
        assertThat(colour1).isNotEqualTo(colour2);
    }
}
