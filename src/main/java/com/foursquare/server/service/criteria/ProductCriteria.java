package com.foursquare.server.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.foursquare.server.domain.Product} entity. This class is used
 * in {@link com.foursquare.server.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private StringFilter name;

    private StringFilter description;

    private BigDecimalFilter expectedPrice;

    private StringFilter provider;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private UUIDFilter productCategoryId;

    private UUIDFilter productImageId;

    private UUIDFilter commentId;

    private UUIDFilter tagId;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.expectedPrice = other.optionalExpectedPrice().map(BigDecimalFilter::copy).orElse(null);
        this.provider = other.optionalProvider().map(StringFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.productCategoryId = other.optionalProductCategoryId().map(UUIDFilter::copy).orElse(null);
        this.productImageId = other.optionalProductImageId().map(UUIDFilter::copy).orElse(null);
        this.commentId = other.optionalCommentId().map(UUIDFilter::copy).orElse(null);
        this.tagId = other.optionalTagId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public UUIDFilter getId() {
        return id;
    }

    public Optional<UUIDFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public UUIDFilter id() {
        if (id == null) {
            setId(new UUIDFilter());
        }
        return id;
    }

    public void setId(UUIDFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BigDecimalFilter getExpectedPrice() {
        return expectedPrice;
    }

    public Optional<BigDecimalFilter> optionalExpectedPrice() {
        return Optional.ofNullable(expectedPrice);
    }

    public BigDecimalFilter expectedPrice() {
        if (expectedPrice == null) {
            setExpectedPrice(new BigDecimalFilter());
        }
        return expectedPrice;
    }

    public void setExpectedPrice(BigDecimalFilter expectedPrice) {
        this.expectedPrice = expectedPrice;
    }

    public StringFilter getProvider() {
        return provider;
    }

    public Optional<StringFilter> optionalProvider() {
        return Optional.ofNullable(provider);
    }

    public StringFilter provider() {
        if (provider == null) {
            setProvider(new StringFilter());
        }
        return provider;
    }

    public void setProvider(StringFilter provider) {
        this.provider = provider;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Optional<StringFilter> optionalLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            setLastModifiedBy(new StringFilter());
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public UUIDFilter getProductCategoryId() {
        return productCategoryId;
    }

    public Optional<UUIDFilter> optionalProductCategoryId() {
        return Optional.ofNullable(productCategoryId);
    }

    public UUIDFilter productCategoryId() {
        if (productCategoryId == null) {
            setProductCategoryId(new UUIDFilter());
        }
        return productCategoryId;
    }

    public void setProductCategoryId(UUIDFilter productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public UUIDFilter getProductImageId() {
        return productImageId;
    }

    public Optional<UUIDFilter> optionalProductImageId() {
        return Optional.ofNullable(productImageId);
    }

    public UUIDFilter productImageId() {
        if (productImageId == null) {
            setProductImageId(new UUIDFilter());
        }
        return productImageId;
    }

    public void setProductImageId(UUIDFilter productImageId) {
        this.productImageId = productImageId;
    }

    public UUIDFilter getCommentId() {
        return commentId;
    }

    public Optional<UUIDFilter> optionalCommentId() {
        return Optional.ofNullable(commentId);
    }

    public UUIDFilter commentId() {
        if (commentId == null) {
            setCommentId(new UUIDFilter());
        }
        return commentId;
    }

    public void setCommentId(UUIDFilter commentId) {
        this.commentId = commentId;
    }

    public UUIDFilter getTagId() {
        return tagId;
    }

    public Optional<UUIDFilter> optionalTagId() {
        return Optional.ofNullable(tagId);
    }

    public UUIDFilter tagId() {
        if (tagId == null) {
            setTagId(new UUIDFilter());
        }
        return tagId;
    }

    public void setTagId(UUIDFilter tagId) {
        this.tagId = tagId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(expectedPrice, that.expectedPrice) &&
            Objects.equals(provider, that.provider) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(productCategoryId, that.productCategoryId) &&
            Objects.equals(productImageId, that.productImageId) &&
            Objects.equals(commentId, that.commentId) &&
            Objects.equals(tagId, that.tagId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            description,
            expectedPrice,
            provider,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            productCategoryId,
            productImageId,
            commentId,
            tagId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalExpectedPrice().map(f -> "expectedPrice=" + f + ", ").orElse("") +
            optionalProvider().map(f -> "provider=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalProductCategoryId().map(f -> "productCategoryId=" + f + ", ").orElse("") +
            optionalProductImageId().map(f -> "productImageId=" + f + ", ").orElse("") +
            optionalCommentId().map(f -> "commentId=" + f + ", ").orElse("") +
            optionalTagId().map(f -> "tagId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
