package com.cordilleracoffee.product.domain.model;

public abstract class BaseImage {
    private Long id;
    private String url;
    private Boolean isPrimary;
    private Integer displayOrder;

    BaseImage(Long id, Integer displayOrder, Boolean isPrimary, String url) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
