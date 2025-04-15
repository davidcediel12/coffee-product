package com.cordilleracoffee.product.infrastructure.persistence.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
public class Variant {

    @Id
    @GeneratedValue
    Long id;

    @Column
    private String name;

    @Lob
    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Boolean isPrimary;

    @Column(nullable = false)
    private String sku;

    @ManyToOne
    private Product product;


    @OneToMany
    private Set<VariantImage> variantImages;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Set<VariantImage> getVariantImages() {
        return variantImages;
    }

    public void setVariantImages(Set<VariantImage> variantImages) {
        this.variantImages = variantImages;
    }
}
