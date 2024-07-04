package com.foursquare.server.domain;

import static com.foursquare.server.domain.OrderItemTestSamples.*;
import static com.foursquare.server.domain.OrderTestSamples.*;
import static com.foursquare.server.domain.ProductCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void productCategoryTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        orderItem.setProductCategory(productCategoryBack);
        assertThat(orderItem.getProductCategory()).isEqualTo(productCategoryBack);

        orderItem.productCategory(null);
        assertThat(orderItem.getProductCategory()).isNull();
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        orderItem.setOrder(orderBack);
        assertThat(orderItem.getOrder()).isEqualTo(orderBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }
}
