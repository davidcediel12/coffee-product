package com.cordilleracoffee.product.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
public class VariantImage {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private Boolean isPrimary;
    @Column(nullable = false)
    private Integer displayOrder;

    @ManyToOne
    private Variant variant;


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

    public Boolean isPrimary() {
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

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }
}
