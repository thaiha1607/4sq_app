package com.foursquare.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Column(name = "name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Column(name = "provider")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String provider;

    @Lob
    @Column(name = "other_info")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String otherInfo;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "colour", "product" }, allowSetters = true)
    private Set<ProductCategory> productCategories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductImage> productImages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_product__tag", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Product id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return this.provider;
    }

    public Product provider(String provider) {
        this.setProvider(provider);
        return this;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getOtherInfo() {
        return this.otherInfo;
    }

    public Product otherInfo(String otherInfo) {
        this.setOtherInfo(otherInfo);
        return this;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    // Inherited createdBy methods
    public Product createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Product createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Product lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Product lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Product setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<ProductCategory> getProductCategories() {
        return this.productCategories;
    }

    public void setProductCategories(Set<ProductCategory> productCategories) {
        if (this.productCategories != null) {
            this.productCategories.forEach(i -> i.setProduct(null));
        }
        if (productCategories != null) {
            productCategories.forEach(i -> i.setProduct(this));
        }
        this.productCategories = productCategories;
    }

    public Product productCategories(Set<ProductCategory> productCategories) {
        this.setProductCategories(productCategories);
        return this;
    }

    public Product addProductCategory(ProductCategory productCategory) {
        this.productCategories.add(productCategory);
        productCategory.setProduct(this);
        return this;
    }

    public Product removeProductCategory(ProductCategory productCategory) {
        this.productCategories.remove(productCategory);
        productCategory.setProduct(null);
        return this;
    }

    public Set<ProductImage> getProductImages() {
        return this.productImages;
    }

    public void setProductImages(Set<ProductImage> productImages) {
        if (this.productImages != null) {
            this.productImages.forEach(i -> i.setProduct(null));
        }
        if (productImages != null) {
            productImages.forEach(i -> i.setProduct(this));
        }
        this.productImages = productImages;
    }

    public Product productImages(Set<ProductImage> productImages) {
        this.setProductImages(productImages);
        return this;
    }

    public Product addProductImage(ProductImage productImage) {
        this.productImages.add(productImage);
        productImage.setProduct(this);
        return this;
    }

    public Product removeProductImage(ProductImage productImage) {
        this.productImages.remove(productImage);
        productImage.setProduct(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Product tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Product addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Product removeTag(Tag tag) {
        this.tags.remove(tag);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", provider='" + getProvider() + "'" +
            ", otherInfo='" + getOtherInfo() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
