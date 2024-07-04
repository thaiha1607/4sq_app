package com.foursquare.server.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.foursquare.server.domain.ProductQuantity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductQuantityDTO implements Serializable {

    private UUID id;

    @NotNull
    @Min(value = 0)
    private Integer qty;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    @NotNull
    private WorkingUnitDTO workingUnit;

    @NotNull
    private ProductCategoryDTO productCategory;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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

    public WorkingUnitDTO getWorkingUnit() {
        return workingUnit;
    }

    public void setWorkingUnit(WorkingUnitDTO workingUnit) {
        this.workingUnit = workingUnit;
    }

    public ProductCategoryDTO getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategoryDTO productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductQuantityDTO)) {
            return false;
        }

        ProductQuantityDTO productQuantityDTO = (ProductQuantityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productQuantityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductQuantityDTO{" +
            "id='" + getId() + "'" +
            ", qty=" + getQty() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", workingUnit=" + getWorkingUnit() +
            ", productCategory=" + getProductCategory() +
            "}";
    }
}
