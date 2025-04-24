package com.cordilleracoffee.product.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Variant {
    private Long id;
    private String name;
    private String description;
    private Stock stock;
    private Money basePrice;
    @JsonProperty("isPrimary")
    private Boolean isPrimary;
    private Sku sku;
    private Set<VariantImage> variantImages;


    public Variant(Long id, String name, String description, Stock stock, Money basePrice,
                   Boolean isPrimary, Sku sku, Set<VariantImage> variantImages) {

        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.stock = Objects.requireNonNull(stock, "stock must not be null");
        this.basePrice = Objects.requireNonNull(basePrice, "basePrice must not be null");
        this.isPrimary = Objects.requireNonNull(isPrimary, "isPrimary must not be null");
        this.sku = Objects.requireNonNull(sku, "sku must not be null");

        if(variantImages == null || variantImages.isEmpty()) {
            throw new IllegalArgumentException("Variant must have images");
        }

        this.variantImages = variantImages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Stock getStock() {
        return stock;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    @JsonProperty("isPrimary")
    public Boolean isPrimary() {
        return isPrimary;
    }

    public Sku getSku() {
        return sku;
    }

    public Set<VariantImage> getVariantImages() {
        return new HashSet<>(variantImages);
    }
}
