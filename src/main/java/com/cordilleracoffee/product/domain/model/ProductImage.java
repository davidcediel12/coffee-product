package com.cordilleracoffee.product.domain.model;

public class ProductImage {

    private Long id;
    private String url;
    private Boolean isPrimary;
    private Integer displayOrder;

    public ProductImage(Integer displayOrder, Boolean isPrimary, String url, Long id) {
        if(displayOrder == null || displayOrder < 0){
            throw new IllegalArgumentException("Display order must be a non-negative integer");
        }
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary != null && isPrimary;

        if(url == null || url.isEmpty()){
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        this.url = url;
        this.id = id;
    }
}
