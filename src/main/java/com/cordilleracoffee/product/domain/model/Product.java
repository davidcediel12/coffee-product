package com.cordilleracoffee.product.domain.model;

import java.util.HashSet;
import java.util.Set;

public class Product {

    private Long id;
    private String sellerId;
    private String name;
    private String description;
    private Sku sku;
    private Stock stock;
    private ProductStatus status;
    private Long categoryId;
    private Money basePrice;
    private Set<ProductImage> images;
    private Set<Variant> variants;
    private Set<Long> tagIds;

    public Product(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.sku = builder.sku;
        this.stock = builder.stock != null ? builder.stock : new Stock(0L);
        this.status = builder.status;
        this.categoryId = builder.categoryId;
        this.basePrice = builder.basePrice;
        this.images = builder.images;
        this.variants = builder.variants;
        this.tagIds = builder.tagIds;
        this.sellerId = builder.sellerId;

        validateProduct();
    }

    private void validateProduct() {

        if (this.basePrice != null && this.variants != null && !this.variants.isEmpty()) {
            throw new IllegalArgumentException("Product cannot have base price and variants");
        }

        if (this.basePrice == null && (this.variants == null || this.variants.isEmpty())) {
            throw new IllegalArgumentException("Product must have base price or variants");
        }

        if (this.images == null || this.images.isEmpty()) {
            throw new IllegalArgumentException("Product must have images");
        }

        if (this.name == null || this.name.isEmpty()) {
            throw new IllegalArgumentException("Product must have name");
        }

        if (this.description == null || this.description.isEmpty()) {
            throw new IllegalArgumentException("Product must have description");
        }

        if (this.sku == null) {
            throw new IllegalArgumentException("Product must have sku");
        }

        if(this.sellerId == null){
            throw new IllegalArgumentException("Product must belong to a seller");
        }


    }

    public String getSellerId() {
        return sellerId;
    }

    public Set<ProductImage> getImages() {
        return new HashSet<>(images);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Sku getSku() {
        return sku;
    }

    public Stock getStock() {
        return stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public Set<Variant> getVariants() {
        return new HashSet<>(variants);
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setVariants(Set<Variant> variants) {
        this.variants = variants;
    }

    public static class Builder {

        private Long id;
        private String name;
        private String description;
        private String sellerId;
        private Sku sku;
        private Stock stock;
        private ProductStatus status;
        private Long categoryId;
        private Money basePrice;
        private Set<ProductImage> images;
        private Set<Variant> variants;
        private Set<Long> tagIds;


        public Builder(String name, String description, String sellerId,
                       Sku sku, Long categoryId, Set<ProductImage> images) {
            this.name = name;
            this.description = description;
            this.sku = sku;
            this.categoryId = categoryId;
            this.images = images;
            this.sellerId = sellerId;
        }


        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder sellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder sku(Sku sku) {
            this.sku = sku;
            return this;
        }

        public Builder stock(Stock stock) {
            this.stock = stock;
            return this;
        }

        public Builder status(ProductStatus status) {
            this.status = status;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder basePrice(Money basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder images(Set<ProductImage> images) {
            this.images = images;
            return this;
        }

        public Builder variants(Set<Variant> variants) {
            this.variants = variants;
            return this;
        }

        public Builder tagIds(Set<Long> tagIds) {
            this.tagIds = tagIds;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
