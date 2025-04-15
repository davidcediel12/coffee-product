package com.cordilleracoffee.product.domain.model;

public class ProductImage extends BaseImage{

    public ProductImage(Long id, Integer displayOrder, Boolean isPrimary, String url) {
        super(id, displayOrder, isPrimary, url);
    }
}
