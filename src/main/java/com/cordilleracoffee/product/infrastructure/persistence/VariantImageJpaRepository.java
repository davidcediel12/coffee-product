package com.cordilleracoffee.product.infrastructure.persistence;

import com.cordilleracoffee.product.infrastructure.persistence.entity.VariantImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantImageJpaRepository extends JpaRepository<VariantImage, Long> {
}