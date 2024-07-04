package com.foursquare.server.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.foursquare.server.domain.InvoiceStatus} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvoiceStatusDTO implements Serializable {

    private Long statusCode;

    @NotNull
    private String description;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    public Long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceStatusDTO)) {
            return false;
        }

        InvoiceStatusDTO invoiceStatusDTO = (InvoiceStatusDTO) o;
        if (this.statusCode == null) {
            return false;
        }
        return Objects.equals(this.statusCode, invoiceStatusDTO.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.statusCode);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InvoiceStatusDTO{" +
            "statusCode=" + getStatusCode() +
            ", description='" + getDescription() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
