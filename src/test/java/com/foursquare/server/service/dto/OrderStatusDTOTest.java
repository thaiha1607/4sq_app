package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusDTO.class);
        OrderStatusDTO orderStatusDTO1 = new OrderStatusDTO();
        orderStatusDTO1.setStatusCode(1L);
        OrderStatusDTO orderStatusDTO2 = new OrderStatusDTO();
        assertThat(orderStatusDTO1).isNotEqualTo(orderStatusDTO2);
        orderStatusDTO2.setStatusCode(orderStatusDTO1.getStatusCode());
        assertThat(orderStatusDTO1).isEqualTo(orderStatusDTO2);
        orderStatusDTO2.setStatusCode(2L);
        assertThat(orderStatusDTO1).isNotEqualTo(orderStatusDTO2);
        orderStatusDTO1.setStatusCode(null);
        assertThat(orderStatusDTO1).isNotEqualTo(orderStatusDTO2);
    }
}
