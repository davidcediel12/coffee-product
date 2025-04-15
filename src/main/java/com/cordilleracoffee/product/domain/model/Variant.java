package com.cordilleracoffee.product.domain.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Variant {
    private String name;
    private String description;
    private Stock stock;
    private Money basePrice;
    private Boolean isPrimary;
    private Sku sku;
    private Set<VariantImage> variantImages;


    public Variant(String name, String description, Stock stock, Money basePrice,
                   Boolean isPrimary, Sku sku, Set<VariantImage> variantImages) {

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

    public Boolean getPrimary() {
        return isPrimary;
    }

    public Sku getSku() {
        return sku;
    }

    public Set<VariantImage> getVariantImages() {
        return new HashSet<>(variantImages);
    }
}
