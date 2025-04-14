package com.cordilleracoffee.product.domain.model;

import java.util.Set;

public class Product {

    private Long id;
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


}
