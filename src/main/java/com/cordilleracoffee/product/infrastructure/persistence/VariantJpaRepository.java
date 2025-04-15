package com.cordilleracoffee.product.infrastructure.persistence;

import com.cordilleracoffee.product.infrastructure.persistence.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantJpaRepository extends JpaRepository<Variant, Long> {
}