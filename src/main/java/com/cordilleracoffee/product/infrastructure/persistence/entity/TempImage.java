package com.cordilleracoffee.product.infrastructure.persistence.entity;


import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record TempImage(
        String id,
        @NotNull
        String name,
        @NotNull
        String url,
        @NotNull
        String userId
) implements Serializable {
}
