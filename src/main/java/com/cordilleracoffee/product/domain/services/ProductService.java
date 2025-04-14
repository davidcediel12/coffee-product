package com.cordilleracoffee.product.domain.services;

import com.cordilleracoffee.product.domain.commands.CreateProduct;

public interface ProductService {
    void createProduct(CreateProduct createProduct);
}
