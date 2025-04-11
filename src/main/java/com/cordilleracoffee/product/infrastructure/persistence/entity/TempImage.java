package com.cordilleracoffee.product.infrastructure.persistence.entity;


import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class TempImage implements Serializable {
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String url;

    @NotNull
    private String userId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
