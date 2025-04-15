package com.cordilleracoffee.product.domain.services;

import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.model.Product;
import jakarta.validation.constraints.NotBlank;

public interface ProductService {
    Product createProduct(CreateProduct createProduct);

    void validateProduct(String userId, @NotBlank String name, @NotBlank String sku);
}
