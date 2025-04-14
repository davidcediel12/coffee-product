package com.cordilleracoffee.product.domain.commands;

import com.cordilleracoffee.product.domain.model.*;

import java.util.List;

public record CreateProduct(
        String userId,
        String name,

        String description,

        Long categoryId,

        Sku sku,

        Stock stock,

        ProductStatus status,

        Money basePrice,

        List<ProductImage> images,

        List<Variant> variants,

        List<Long> tagsIds
) {


}
