package com.foursquare.server.domain;

import static com.foursquare.server.domain.AddressTestSamples.*;
import static com.foursquare.server.domain.WorkingUnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkingUnitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkingUnit.class);
        WorkingUnit workingUnit1 = getWorkingUnitSample1();
        WorkingUnit workingUnit2 = new WorkingUnit();
        assertThat(workingUnit1).isNotEqualTo(workingUnit2);

        workingUnit2.setId(workingUnit1.getId());
        assertThat(workingUnit1).isEqualTo(workingUnit2);

        workingUnit2 = getWorkingUnitSample2();
        assertThat(workingUnit1).isNotEqualTo(workingUnit2);
    }

    @Test
    void addressTest() {
        WorkingUnit workingUnit = getWorkingUnitRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        workingUnit.setAddress(addressBack);
        assertThat(workingUnit.getAddress()).isEqualTo(addressBack);

        workingUnit.address(null);
        assertThat(workingUnit.getAddress()).isNull();
    }
}
