package com.foursquare.server.domain;

import static com.foursquare.server.domain.InternalOrderHistoryTestSamples.*;
import static com.foursquare.server.domain.InternalOrderTestSamples.*;
import static com.foursquare.server.domain.OrderStatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InternalOrderHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalOrderHistory.class);
        InternalOrderHistory internalOrderHistory1 = getInternalOrderHistorySample1();
        InternalOrderHistory internalOrderHistory2 = new InternalOrderHistory();
        assertThat(internalOrderHistory1).isNotEqualTo(internalOrderHistory2);

        internalOrderHistory2.setId(internalOrderHistory1.getId());
        assertThat(internalOrderHistory1).isEqualTo(internalOrderHistory2);

        internalOrderHistory2 = getInternalOrderHistorySample2();
        assertThat(internalOrderHistory1).isNotEqualTo(internalOrderHistory2);
    }

    @Test
    void statusTest() {
        InternalOrderHistory internalOrderHistory = getInternalOrderHistoryRandomSampleGenerator();
        OrderStatus orderStatusBack = getOrderStatusRandomSampleGenerator();

        internalOrderHistory.setStatus(orderStatusBack);
        assertThat(internalOrderHistory.getStatus()).isEqualTo(orderStatusBack);

        internalOrderHistory.status(null);
        assertThat(internalOrderHistory.getStatus()).isNull();
    }

    @Test
    void orderTest() {
        InternalOrderHistory internalOrderHistory = getInternalOrderHistoryRandomSampleGenerator();
        InternalOrder internalOrderBack = getInternalOrderRandomSampleGenerator();

        internalOrderHistory.setOrder(internalOrderBack);
        assertThat(internalOrderHistory.getOrder()).isEqualTo(internalOrderBack);

        internalOrderHistory.order(null);
        assertThat(internalOrderHistory.getOrder()).isNull();
    }
}
