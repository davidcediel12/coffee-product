package com.cordilleracoffee.product.domain.model;

public record Sku(String sku) {

    public Sku {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be null or blank");
        }
        if (sku.length() > 50) {
            throw new IllegalArgumentException("SKU cannot be longer than 50 characters");
        }
    }
}
