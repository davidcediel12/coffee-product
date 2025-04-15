package com.cordilleracoffee.product.domain.repository;

import com.cordilleracoffee.product.domain.model.Product;

public interface ProductRepository {
    Long save(Product product);

    boolean existsByUserAndName(String userId, String name);

    boolean existByUserAndSku(String userId, String sku);
}
