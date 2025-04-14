package com.cordilleracoffee.product.domain.model;

import java.util.Objects;

public class Category {

    private Long id;
    private String name;
    private String description;


    public Category(String name, String description) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }
}
