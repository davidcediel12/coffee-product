package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.infrastructure.persistence.ProductJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Long save(Product product) {
        return 0L;
    }

    @Override
    public boolean existsByUserAndName(String userId, String name) {
        return productJpaRepository.existsByUserIdAndName(userId, name);
    }

    @Override
    public boolean existByUserAndSku(String userId, String sku) {
        return productJpaRepository.existsByUserIdAndSku(userId, sku);
    }
}
