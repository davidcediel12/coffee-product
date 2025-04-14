package com.cordilleracoffee.product.domain.model;

import java.util.Objects;

public class Variant {
    private String name;
    private String description;
    private Stock stock;
    private Money basePrice;
    private Boolean isPrimary;
    private Sku sku;


    public Variant(String name, String description, Stock stock, Money basePrice, Boolean isPrimary, Sku sku) {

        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.stock = Objects.requireNonNull(stock, "stock must not be null");
        this.basePrice = Objects.requireNonNull(basePrice, "basePrice must not be null");
        this.isPrimary = Objects.requireNonNull(isPrimary, "isPrimary must not be null");
        this.sku = Objects.requireNonNull(sku, "sku must not be null");
    }

}
