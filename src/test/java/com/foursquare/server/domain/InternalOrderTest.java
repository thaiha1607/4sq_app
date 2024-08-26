package com.foursquare.server.domain;

import static com.foursquare.server.domain.InternalOrderHistoryTestSamples.*;
import static com.foursquare.server.domain.InternalOrderTestSamples.*;
import static com.foursquare.server.domain.OrderStatusTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InternalOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalOrder.class);
        InternalOrder internalOrder1 = getInternalOrderSample1();
        InternalOrder internalOrder2 = new InternalOrder();
        assertThat(internalOrder1).isNotEqualTo(internalOrder2);

        internalOrder2.setId(internalOrder1.getId());
        assertThat(internalOrder1).isEqualTo(internalOrder2);

        internalOrder2 = getInternalOrderSample2();
        assertThat(internalOrder1).isNotEqualTo(internalOrder2);
    }

    @Test
    void historyTest() {
        InternalOrder internalOrder = getInternalOrderRandomSampleGenerator();
        InternalOrderHistory internalOrderHistoryBack = getInternalOrderHistoryRandomSampleGenerator();

        internalOrder.addHistory(internalOrderHistoryBack);
        assertThat(internalOrder.getHistories()).containsOnly(internalOrderHistoryBack);
        assertThat(internalOrderHistoryBack.getOrder()).isEqualTo(internalOrder);

        internalOrder.removeHistory(internalOrderHistoryBack);
        assertThat(internalOrder.getHistories()).doesNotContain(internalOrderHistoryBack);
        assertThat(internalOrderHistoryBack.getOrder()).isNull();

        internalOrder.histories(new HashSet<>(Set.of(internalOrderHistoryBack)));
        assertThat(internalOrder.getHistories()).containsOnly(internalOrderHistoryBack);
        assertThat(internalOrderHistoryBack.getOrder()).isEqualTo(internalOrder);

        internalOrder.setHistories(new HashSet<>());
        assertThat(internalOrder.getHistories()).doesNotContain(internalOrderHistoryBack);
        assertThat(internalOrderHistoryBack.getOrder()).isNull();
    }

    @Test
    void statusTest() {
        InternalOrder internalOrder = getInternalOrderRandomSampleGenerator();
        OrderStatus orderStatusBack = getOrderStatusRandomSampleGenerator();

        internalOrder.setStatus(orderStatusBack);
        assertThat(internalOrder.getStatus()).isEqualTo(orderStatusBack);

        internalOrder.status(null);
        assertThat(internalOrder.getStatus()).isNull();
    }

    @Test
    void rootOrderTest() {
        InternalOrder internalOrder = getInternalOrderRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        internalOrder.setRootOrder(orderBack);
        assertThat(internalOrder.getRootOrder()).isEqualTo(orderBack);

        internalOrder.rootOrder(null);
        assertThat(internalOrder.getRootOrder()).isNull();
    }
}
