package com.foursquare.server.domain;

import static com.foursquare.server.domain.InternalOrderTestSamples.*;
import static com.foursquare.server.domain.WarehouseAssignmentTestSamples.*;
import static com.foursquare.server.domain.WorkingUnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WarehouseAssignment.class);
        WarehouseAssignment warehouseAssignment1 = getWarehouseAssignmentSample1();
        WarehouseAssignment warehouseAssignment2 = new WarehouseAssignment();
        assertThat(warehouseAssignment1).isNotEqualTo(warehouseAssignment2);

        warehouseAssignment2.setId(warehouseAssignment1.getId());
        assertThat(warehouseAssignment1).isEqualTo(warehouseAssignment2);

        warehouseAssignment2 = getWarehouseAssignmentSample2();
        assertThat(warehouseAssignment1).isNotEqualTo(warehouseAssignment2);
    }

    @Test
    void sourceWorkingUnitTest() {
        WarehouseAssignment warehouseAssignment = getWarehouseAssignmentRandomSampleGenerator();
        WorkingUnit workingUnitBack = getWorkingUnitRandomSampleGenerator();

        warehouseAssignment.setSourceWorkingUnit(workingUnitBack);
        assertThat(warehouseAssignment.getSourceWorkingUnit()).isEqualTo(workingUnitBack);

        warehouseAssignment.sourceWorkingUnit(null);
        assertThat(warehouseAssignment.getSourceWorkingUnit()).isNull();
    }

    @Test
    void targetWorkingUnitTest() {
        WarehouseAssignment warehouseAssignment = getWarehouseAssignmentRandomSampleGenerator();
        WorkingUnit workingUnitBack = getWorkingUnitRandomSampleGenerator();

        warehouseAssignment.setTargetWorkingUnit(workingUnitBack);
        assertThat(warehouseAssignment.getTargetWorkingUnit()).isEqualTo(workingUnitBack);

        warehouseAssignment.targetWorkingUnit(null);
        assertThat(warehouseAssignment.getTargetWorkingUnit()).isNull();
    }

    @Test
    void internalOrderTest() {
        WarehouseAssignment warehouseAssignment = getWarehouseAssignmentRandomSampleGenerator();
        InternalOrder internalOrderBack = getInternalOrderRandomSampleGenerator();

        warehouseAssignment.setInternalOrder(internalOrderBack);
        assertThat(warehouseAssignment.getInternalOrder()).isEqualTo(internalOrderBack);

        warehouseAssignment.internalOrder(null);
        assertThat(warehouseAssignment.getInternalOrder()).isNull();
    }
}
