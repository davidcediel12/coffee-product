package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public Long save(Product product) {
        return 0L;
    }

    @Override
    public boolean existsByUserAndName(String userId, String name) {
        return false;
    }

    @Override
    public boolean existByUserAndSku(String userId, String sku) {
        return false;
    }
}
