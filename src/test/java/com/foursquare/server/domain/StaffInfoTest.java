package com.foursquare.server.domain;

import static com.foursquare.server.domain.StaffInfoTestSamples.*;
import static com.foursquare.server.domain.WorkingUnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StaffInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StaffInfo.class);
        StaffInfo staffInfo1 = getStaffInfoSample1();
        StaffInfo staffInfo2 = new StaffInfo();
        assertThat(staffInfo1).isNotEqualTo(staffInfo2);

        staffInfo2.setId(staffInfo1.getId());
        assertThat(staffInfo1).isEqualTo(staffInfo2);

        staffInfo2 = getStaffInfoSample2();
        assertThat(staffInfo1).isNotEqualTo(staffInfo2);
    }

    @Test
    void workingUnitTest() {
        StaffInfo staffInfo = getStaffInfoRandomSampleGenerator();
        WorkingUnit workingUnitBack = getWorkingUnitRandomSampleGenerator();

        staffInfo.setWorkingUnit(workingUnitBack);
        assertThat(staffInfo.getWorkingUnit()).isEqualTo(workingUnitBack);

        staffInfo.workingUnit(null);
        assertThat(staffInfo.getWorkingUnit()).isNull();
    }
}
