package com.foursquare.server.domain;

import static com.foursquare.server.domain.ProductCategoryTestSamples.*;
import static com.foursquare.server.domain.ProductQuantityTestSamples.*;
import static com.foursquare.server.domain.WorkingUnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductQuantityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductQuantity.class);
        ProductQuantity productQuantity1 = getProductQuantitySample1();
        ProductQuantity productQuantity2 = new ProductQuantity();
        assertThat(productQuantity1).isNotEqualTo(productQuantity2);

        productQuantity2.setId(productQuantity1.getId());
        assertThat(productQuantity1).isEqualTo(productQuantity2);

        productQuantity2 = getProductQuantitySample2();
        assertThat(productQuantity1).isNotEqualTo(productQuantity2);
    }

    @Test
    void workingUnitTest() {
        ProductQuantity productQuantity = getProductQuantityRandomSampleGenerator();
        WorkingUnit workingUnitBack = getWorkingUnitRandomSampleGenerator();

        productQuantity.setWorkingUnit(workingUnitBack);
        assertThat(productQuantity.getWorkingUnit()).isEqualTo(workingUnitBack);

        productQuantity.workingUnit(null);
        assertThat(productQuantity.getWorkingUnit()).isNull();
    }

    @Test
    void productCategoryTest() {
        ProductQuantity productQuantity = getProductQuantityRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        productQuantity.setProductCategory(productCategoryBack);
        assertThat(productQuantity.getProductCategory()).isEqualTo(productCategoryBack);

        productQuantity.productCategory(null);
        assertThat(productQuantity.getProductCategory()).isNull();
    }
}
