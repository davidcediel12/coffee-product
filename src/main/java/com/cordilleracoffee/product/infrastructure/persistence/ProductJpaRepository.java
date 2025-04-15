package com.cordilleracoffee.product.infrastructure.persistence;

import com.cordilleracoffee.product.infrastructure.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {


    Boolean existsBySellerIdAndName(String userId, String name);
    Boolean existsBySellerIdAndSku(String userId, String sku);

}