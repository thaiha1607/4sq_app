package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductImageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductImageDTO.class);
        ProductImageDTO productImageDTO1 = new ProductImageDTO();
        productImageDTO1.setId(UUID.randomUUID());
        ProductImageDTO productImageDTO2 = new ProductImageDTO();
        assertThat(productImageDTO1).isNotEqualTo(productImageDTO2);
        productImageDTO2.setId(productImageDTO1.getId());
        assertThat(productImageDTO1).isEqualTo(productImageDTO2);
        productImageDTO2.setId(UUID.randomUUID());
        assertThat(productImageDTO1).isNotEqualTo(productImageDTO2);
        productImageDTO1.setId(null);
        assertThat(productImageDTO1).isNotEqualTo(productImageDTO2);
    }
}
