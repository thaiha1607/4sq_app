package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductQuantityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductQuantityDTO.class);
        ProductQuantityDTO productQuantityDTO1 = new ProductQuantityDTO();
        productQuantityDTO1.setId(UUID.randomUUID());
        ProductQuantityDTO productQuantityDTO2 = new ProductQuantityDTO();
        assertThat(productQuantityDTO1).isNotEqualTo(productQuantityDTO2);
        productQuantityDTO2.setId(productQuantityDTO1.getId());
        assertThat(productQuantityDTO1).isEqualTo(productQuantityDTO2);
        productQuantityDTO2.setId(UUID.randomUUID());
        assertThat(productQuantityDTO1).isNotEqualTo(productQuantityDTO2);
        productQuantityDTO1.setId(null);
        assertThat(productQuantityDTO1).isNotEqualTo(productQuantityDTO2);
    }
}
