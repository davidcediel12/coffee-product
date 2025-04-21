package com.cordilleracoffee.product.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseImage {
    private Long id;
    private String name;
    private String url;
    @JsonProperty("isPrimary")
    private Boolean isPrimary;
    private Integer displayOrder;

    BaseImage(Long id, String name, Integer displayOrder, Boolean isPrimary, String url) {
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


        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Image name cannot be null or empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Image name cannot be null or empty");
        }
        this.name = name;
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

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Image url cannot be null or empty");
        }
        this.url = url;
    }

    @JsonProperty("isPrimary")
    public Boolean isPrimary() {
        return isPrimary;
    }

    @JsonProperty("isPrimary")
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
