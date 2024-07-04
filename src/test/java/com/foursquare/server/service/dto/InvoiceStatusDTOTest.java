package com.foursquare.server.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.foursquare.server.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvoiceStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceStatusDTO.class);
        InvoiceStatusDTO invoiceStatusDTO1 = new InvoiceStatusDTO();
        invoiceStatusDTO1.setStatusCode(1L);
        InvoiceStatusDTO invoiceStatusDTO2 = new InvoiceStatusDTO();
        assertThat(invoiceStatusDTO1).isNotEqualTo(invoiceStatusDTO2);
        invoiceStatusDTO2.setStatusCode(invoiceStatusDTO1.getStatusCode());
        assertThat(invoiceStatusDTO1).isEqualTo(invoiceStatusDTO2);
        invoiceStatusDTO2.setStatusCode(2L);
        assertThat(invoiceStatusDTO1).isNotEqualTo(invoiceStatusDTO2);
        invoiceStatusDTO1.setStatusCode(null);
        assertThat(invoiceStatusDTO1).isNotEqualTo(invoiceStatusDTO2);
    }
}
