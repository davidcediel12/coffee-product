package com.cordilleracoffee.product.domain.repository;

import com.cordilleracoffee.product.domain.model.Product;

public interface ProductRepository {
    Long save(Product product);
}
