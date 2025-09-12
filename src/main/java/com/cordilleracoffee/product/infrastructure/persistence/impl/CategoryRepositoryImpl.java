package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.repository.CategoryRepository;
import com.cordilleracoffee.product.infrastructure.persistence.CategoryJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryRepositoryImpl  implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryRepositoryImpl(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public boolean exists(Long id) {
        return categoryJpaRepository.existsById(id);
    }
}
