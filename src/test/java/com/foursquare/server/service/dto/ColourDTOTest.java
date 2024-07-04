package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ColourDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ColourDTO.class);
        ColourDTO colourDTO1 = new ColourDTO();
        colourDTO1.setId(UUID.randomUUID());
        ColourDTO colourDTO2 = new ColourDTO();
        assertThat(colourDTO1).isNotEqualTo(colourDTO2);
        colourDTO2.setId(colourDTO1.getId());
        assertThat(colourDTO1).isEqualTo(colourDTO2);
        colourDTO2.setId(UUID.randomUUID());
        assertThat(colourDTO1).isNotEqualTo(colourDTO2);
        colourDTO1.setId(null);
        assertThat(colourDTO1).isNotEqualTo(colourDTO2);
    }
}
