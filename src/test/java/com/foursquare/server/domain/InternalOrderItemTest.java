package com.foursquare.server.domain;

import static com.foursquare.server.domain.InternalOrderItemTestSamples.*;
import static com.foursquare.server.domain.OrderItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InternalOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InternalOrderItem.class);
        InternalOrderItem internalOrderItem1 = getInternalOrderItemSample1();
        InternalOrderItem internalOrderItem2 = new InternalOrderItem();
        assertThat(internalOrderItem1).isNotEqualTo(internalOrderItem2);

        internalOrderItem2.setId(internalOrderItem1.getId());
        assertThat(internalOrderItem1).isEqualTo(internalOrderItem2);

        internalOrderItem2 = getInternalOrderItemSample2();
        assertThat(internalOrderItem1).isNotEqualTo(internalOrderItem2);
    }

    @Test
    void orderItemTest() {
        InternalOrderItem internalOrderItem = getInternalOrderItemRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        internalOrderItem.setOrderItem(orderItemBack);
        assertThat(internalOrderItem.getOrderItem()).isEqualTo(orderItemBack);

        internalOrderItem.orderItem(null);
        assertThat(internalOrderItem.getOrderItem()).isNull();
    }
}
