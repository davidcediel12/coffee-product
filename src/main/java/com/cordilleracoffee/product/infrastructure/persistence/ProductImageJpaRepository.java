package com.cordilleracoffee.product.infrastructure.persistence;

import com.cordilleracoffee.product.infrastructure.persistence.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageJpaRepository extends JpaRepository<ProductImage, Long> {
}