package com.cordilleracoffee.product.domain.model;

public class ProductImage extends BaseImage {


    public ProductImage(Long id, String name, Integer displayOrder, Boolean isPrimary, String url) {
        super(id, name, displayOrder, isPrimary, url);
    }
}
