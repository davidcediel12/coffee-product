package com.cordilleracoffee.product.domain.model;

import org.springframework.util.Assert;

public record TemporalImage(String id, String name, String url, String userId) {

    public TemporalImage {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(name, "name must not be null");
        Assert.notNull(url, "url must not be null");
        Assert.notNull(userId, "user id must not be null");
    }
}
