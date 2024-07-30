package com.foursquare.server.domain;

import static com.foursquare.server.domain.InvoiceStatusTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceStatus.class);
        InvoiceStatus invoiceStatus1 = getInvoiceStatusSample1();
        InvoiceStatus invoiceStatus2 = new InvoiceStatus();
        assertThat(invoiceStatus1).isNotEqualTo(invoiceStatus2);

        invoiceStatus2.setId(invoiceStatus1.getId());
        assertThat(invoiceStatus1).isEqualTo(invoiceStatus2);

        invoiceStatus2 = getInvoiceStatusSample2();
        assertThat(invoiceStatus1).isNotEqualTo(invoiceStatus2);
    }
}
