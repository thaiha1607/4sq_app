package com.foursquare.server.domain;

import static com.foursquare.server.domain.OrderHistoryTestSamples.*;
import static com.foursquare.server.domain.OrderStatusTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderHistory.class);
        OrderHistory orderHistory1 = getOrderHistorySample1();
        OrderHistory orderHistory2 = new OrderHistory();
        assertThat(orderHistory1).isNotEqualTo(orderHistory2);

        orderHistory2.setId(orderHistory1.getId());
        assertThat(orderHistory1).isEqualTo(orderHistory2);

        orderHistory2 = getOrderHistorySample2();
        assertThat(orderHistory1).isNotEqualTo(orderHistory2);
    }

    @Test
    void statusTest() {
        OrderHistory orderHistory = getOrderHistoryRandomSampleGenerator();
        OrderStatus orderStatusBack = getOrderStatusRandomSampleGenerator();

        orderHistory.setStatus(orderStatusBack);
        assertThat(orderHistory.getStatus()).isEqualTo(orderStatusBack);

        orderHistory.status(null);
        assertThat(orderHistory.getStatus()).isNull();
    }

    @Test
    void orderTest() {
        OrderHistory orderHistory = getOrderHistoryRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        orderHistory.setOrder(orderBack);
        assertThat(orderHistory.getOrder()).isEqualTo(orderBack);

        orderHistory.order(null);
        assertThat(orderHistory.getOrder()).isNull();
    }
}
