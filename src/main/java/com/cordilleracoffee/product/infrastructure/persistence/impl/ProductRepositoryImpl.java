package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;

public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public Long save(Product product) {
        return 0L;
    }
}
