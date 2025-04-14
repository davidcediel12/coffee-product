package com.cordilleracoffee.product.domain.model;

public record Stock(Long amount) {

    public Stock {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be greater than or equal to 0");
        }
    }
}
